package com.github.aivanovski.testswithme.web.domain.sync

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.extensions.sha256
import com.github.aivanovski.testswithme.extensions.trimLines
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.flow.yaml.YamlParser
import com.github.aivanovski.testswithme.web.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.data.repository.TestSourceRepository
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.domain.ReferenceResolver
import com.github.aivanovski.testswithme.web.domain.trees.TreeDiffer
import com.github.aivanovski.testswithme.web.domain.trees.model.DiffEvent
import com.github.aivanovski.testswithme.web.domain.trees.model.NodeType
import com.github.aivanovski.testswithme.web.domain.usecases.CloneGitRepositoryUseCase
import com.github.aivanovski.testswithme.web.domain.usecases.GetLocalRepositoryLastCommitUseCase
import com.github.aivanovski.testswithme.web.entity.AbsolutePath
import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.RelativePath
import com.github.aivanovski.testswithme.web.entity.SyncItemType
import com.github.aivanovski.testswithme.web.entity.TestSource
import com.github.aivanovski.testswithme.web.entity.Timestamp
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundException
import com.github.aivanovski.testswithme.web.extensions.getName
import com.github.aivanovski.testswithme.web.extensions.toRelative
import org.slf4j.LoggerFactory

class FlowSynchronizer(
    private val syncUid: Uid,
    private val source: TestSource
) {

    private val projectRepository: ProjectRepository by lazy { get() }
    private val groupRepository: GroupRepository by lazy { get() }
    private val flowRepository: FlowRepository by lazy { get() }
    private val testSourceRepository: TestSourceRepository by lazy { get() }
    private val fileSystemProvider: FileSystemProvider by lazy { get() }
    private val referenceResolver: ReferenceResolver by lazy { get() }
    private val cloneRepoUseCase: CloneGitRepositoryUseCase by lazy { get() }
    private val getLastCommitUseCase: GetLocalRepositoryLastCommitUseCase by lazy { get() }
    private val projectTreeBuilder = ProjectTreeBuilder(groupRepository, flowRepository)
    private val repositoryTreeBuilder = RepositoryTreeBuilder(fileSystemProvider)

    private var cleanUpDir: RelativePath? = null

    fun cleanUp(): Either<AppException, Unit> =
        either {
            val dir = cleanUpDir
            if (dir != null) {
                fileSystemProvider.remove(dir).bind()
            }
            cleanUpDir = null
        }

    fun sync(): Either<AppException, Pair<TestSource, List<ProcessedSyncItem>>> =
        either {
            logger.debug("Checking url: {}", source.repositoryUrl)

            val repoDir = cloneRepoUseCase.cloneGitRepository(source.repositoryUrl)
                .bind()

            cleanUpDir = repoDir.toRelative()

            val testRootDir = fileSystemProvider.listFiles(repoDir.toRelative())
                .bind()
                .firstOrNull { file -> file.getName() == TEST_ROOT_DIRECTORY }

            if (testRootDir == null) {
                fileSystemProvider.remove(repoDir.toRelative()).bind()

                return@either (source to emptyList())
            }

            val (pathToFileMap, repoTree) = repositoryTreeBuilder.buildRepositoryTree(
                root = testRootDir.toRelative()
            ).bind()

            val project = getProject(testSourceUid = source.uid).bind()

            val projectTree = projectTreeBuilder.buildProjectTree(project).bind()

            val diff = TreeDiffer.getDiff(
                lhs = projectTree,
                rhs = repoTree,
                isContentChanged = { lhs, rhs ->
                    val flowFile = pathToFileMap[rhs.path] ?: return@getDiff false

                    isContentChanged(
                        project = project,
                        flowPathAndName = lhs.path,
                        file = flowFile.toRelative()
                    ).bind()
                }
            )

            val processedItems = processDiff(
                project = project,
                diff = diff,
                pathToFileMap = pathToFileMap
            ).bind()

            val commitHash = getLastCommitUseCase.getLastCommitHash(repoDir).bind()

            val updatedSource = testSourceRepository.update(
                source.copy(
                    lastCommitHash = commitHash,
                    lastCheckTimestamp = Timestamp.now(),
                    isForceSyncFlag = false
                )
            ).bind()

            (updatedSource to processedItems)
        }

    private fun isContentChanged(
        project: Project,
        flowPathAndName: String,
        file: RelativePath
    ): Either<AppException, Boolean> =
        either {
            val flow = referenceResolver.resolveFlowByPathOrName(
                pathOrName = flowPathAndName,
                projectUid = project.uid
            ).bind()

            val oldHash = flow.contentHash

            val newContent = fileSystemProvider.readBytes(file)
                .map { bytes -> String(bytes) }
                .bind()

            val newHash = newContent.trimLines().sha256()

            (oldHash != newHash)
        }

    private fun getProject(testSourceUid: Uid): Either<AppException, Project> =
        either {
            projectRepository.getAll().bind()
                .firstOrNull { project -> project.testSourceUid == testSourceUid }
                ?: raise(
                    EntityNotFoundException(
                        entity = Project::class.java.simpleName,
                        key = "testSourceUid",
                        value = testSourceUid.toString()
                    )
                )
        }

    private fun processDiff(
        project: Project,
        diff: List<DiffEvent>,
        pathToFileMap: Map<String, AbsolutePath>
    ): Either<AppException, List<ProcessedSyncItem>> =
        either {
            val groupsToInsert = diff.mapNotNull { event ->
                if (event is DiffEvent.Insert && event.node.type == NodeType.BRANCH) {
                    event
                } else {
                    null
                }
            }

            val flowsToInsert = diff.mapNotNull { event ->
                if (event is DiffEvent.Insert && event.node.type == NodeType.LEAF) {
                    event
                } else {
                    null
                }
            }

            val flowsToUpdate = diff.mapNotNull { event ->
                if (event is DiffEvent.Update && event.newNode.type == NodeType.LEAF) {
                    event
                } else {
                    null
                }
            }

            logger.debug(
                "Diff: totalEvents={}, groupToInsert={}, flowsToInsert={}, flowsToUpdate={}",
                groupsToInsert.size + flowsToInsert.size + flowsToUpdate.size,
                groupsToInsert.size,
                flowsToInsert.size,
                flowsToUpdate.size
            )
            for (event in groupsToInsert) {
                logger.debug("    {}", event.format())
            }
            for (event in flowsToInsert) {
                logger.debug("    {}", event.format())
            }
            for (event in flowsToUpdate) {
                logger.debug("    {}", event.format())
            }

            val insertedGroups = insertGroups(
                project = project,
                events = groupsToInsert
            ).bind()

            val insertedFlows = insertFlows(
                project = project,
                events = flowsToInsert,
                pathToFileMap = pathToFileMap
            ).bind()

            val updatedFlows = updateFlows(
                project = project,
                events = flowsToUpdate,
                pathToFileMap = pathToFileMap
            ).bind()

            insertedGroups + insertedFlows + updatedFlows
        }

    private fun insertGroups(
        project: Project,
        events: List<DiffEvent.Insert>
    ): Either<AppException, List<ProcessedSyncItem>> =
        either {
            val processedItems = mutableListOf<ProcessedSyncItem>()

            val sortedEvents = events.sortedBy { event -> event.node.path.length }

            for (event in sortedEvents) {
                val nodePath = event.node.path

                val parentUids = resolveParentGroupUids(
                    project = project,
                    path = nodePath
                ).bind()

                val name = nodePath.split("/").last()

                val newGroup = groupRepository.add(
                    Group(
                        uid = Uid.generate(),
                        parentUid = parentUids.last(),
                        projectUid = project.uid,
                        name = name,
                        isDeleted = false
                    )
                ).bind()

                processedItems.add(
                    ProcessedSyncItem(
                        syncUid = syncUid,
                        entityUid = newGroup.uid,
                        isSuccess = true,
                        type = SyncItemType.INSERT_GROUP,
                        path = nodePath
                    )
                )
            }

            processedItems
        }

    private fun insertFlows(
        project: Project,
        events: List<DiffEvent.Insert>,
        pathToFileMap: Map<String, AbsolutePath>
    ): Either<AppException, List<ProcessedSyncItem>> =
        either {
            val processedItems = mutableListOf<ProcessedSyncItem>()

            for (event in events) {
                val nodePath = event.node.path
                val file = pathToFileMap[nodePath] ?: continue

                val parentUids = resolveParentGroupUids(
                    project = project,
                    path = nodePath
                ).bind()

                val flowFileName = nodePath.split("/").last()
                val parentUid = parentUids.last()

                val content = fileSystemProvider.readBytes(file.toRelative())
                    .map { bytes -> String(bytes) }
                    .bind()

                val parseYamlFlowResult = YamlParser().parse(content)
                    .mapLeft { error -> AppException(cause = error) }

                if (parseYamlFlowResult.isLeft()) {
                    ProcessedSyncItem(
                        syncUid = syncUid,
                        entityUid = null,
                        type = SyncItemType.INSERT_FLOW,
                        isSuccess = false,
                        path = nodePath
                    )
                    continue
                }

                val yamlFlow = parseYamlFlowResult.unwrap()

                // TODO: handle group reference inside YAML file

                val newFlow = flowRepository.add(
                    flow = Flow(
                        uid = Uid.generate(),
                        projectUid = project.uid,
                        groupUid = parentUid,
                        name = yamlFlow.name.ifEmpty { flowFileName },
                        contentHash = content.trimLines().sha256(),
                        isDeleted = false
                    ),
                    content = content
                ).bind()

                processedItems.add(
                    ProcessedSyncItem(
                        syncUid = syncUid,
                        entityUid = newFlow.uid,
                        type = SyncItemType.INSERT_FLOW,
                        isSuccess = true,
                        path = nodePath
                    )
                )
            }

            processedItems
        }

    private fun updateFlows(
        project: Project,
        events: List<DiffEvent.Update>,
        pathToFileMap: Map<String, AbsolutePath>
    ): Either<AppException, List<ProcessedSyncItem>> =
        either {
            val processedItems = mutableListOf<ProcessedSyncItem>()

            for (event in events) {
                val nodePath = event.newNode.path
                val file = pathToFileMap[nodePath] ?: continue

                val parentUids = resolveParentGroupUids(
                    project = project,
                    path = nodePath
                ).bind()

                val parentUid = parentUids.last()
                val flowFileName = nodePath.split("/").last()

                val content = fileSystemProvider.readBytes(file.toRelative())
                    .map { bytes -> String(bytes) }
                    .bind()

                val parseYamlFlowResult = YamlParser().parse(content)
                    .mapLeft { error -> AppException(cause = error) }

                if (parseYamlFlowResult.isLeft()) {
                    processedItems.add(
                        ProcessedSyncItem(
                            syncUid = syncUid,
                            entityUid = null,
                            type = SyncItemType.UPDATE_FLOW,
                            isSuccess = false,
                            path = nodePath
                        )
                    )
                    continue
                }

                val yamlFlow = parseYamlFlowResult.unwrap()
                val flowName = yamlFlow.name.ifEmpty { flowFileName }

                // TODO: handle group reference inside YAML file

                val flows = flowRepository.getFlowsByProjectAndGroup(
                    userUid = project.userUid,
                    projectUid = project.uid,
                    groupUid = parentUid
                ).bind()

                val oldFlow = flows.firstOrNull { flow -> flow.name == flowName }
                    ?: raise(AppException("Failed to find flow to update"))

                val newFlow = flowRepository.update(
                    flow = oldFlow.copy(
                        contentHash = content.trimLines().sha256(),
                        isDeleted = false
                    ),
                    content = content
                ).bind()

                processedItems.add(
                    ProcessedSyncItem(
                        syncUid = syncUid,
                        entityUid = newFlow.uid,
                        type = SyncItemType.UPDATE_FLOW,
                        isSuccess = true,
                        path = nodePath
                    )
                )
            }

            processedItems
        }

    private fun resolveParentGroupUids(
        project: Project,
        path: String
    ): Either<AppException, List<Uid>> =
        either {
            val names = path.split("/").dropLast(1)
            if (names.isEmpty()) {
                raise(AppException("Failed to determine names"))
            }

            val allGroups = groupRepository.getByProjectUid(project.uid).bind()

            val parentUids = if (names.isNotEmpty()) {
                val parentGroups = referenceResolver.resolveGroupsByNames(
                    names = names,
                    groups = allGroups
                ).bind()

                parentGroups.map { group -> group.uid }
            } else {
                listOf(project.rootGroupUid)
            }
            if (parentUids.isEmpty()) {
                raise(AppException("Failed to determine parent group"))
            }

            parentUids
        }

    private fun DiffEvent.format(): String {
        return when (this) {
            is DiffEvent.Insert -> "INSERT ${node.type} ${node.path}"
            is DiffEvent.Delete -> "DELETE ${node.type} ${node.path}"
            is DiffEvent.Update -> "UPDATE ${newNode.type} ${newNode.path}"
        }
    }

    companion object {
        private const val TEST_ROOT_DIRECTORY = "tests"
        private val logger = LoggerFactory.getLogger(FlowSynchronizer::class.java)
    }
}