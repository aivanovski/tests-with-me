package com.github.aivanovski.testswithme.android.domain.flow

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.GroupRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.domain.buildGroupTree
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerInteractor.Companion.LOCAL_PROJECT_UID
import com.github.aivanovski.testswithme.android.entity.TreeNode
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByNameException
import com.github.aivanovski.testswithme.flow.reference.ReferenceResolverUtils.resolveGroupsAndName

class ReferenceResolver(
    private val flowRepository: FlowRepository,
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository
) {

    suspend fun resolveProjectByName(projectName: String): Either<AppException, ProjectEntry> =
        either {
            val projects = projectRepository.getProjects().bind()

            val project = projects.firstOrNull { project ->
                project.name.contentEquals(projectName, ignoreCase = true)
            }
                ?: raise(FailedToFindEntityByNameException(ProjectEntry::class, projectName))

            project
        }

    suspend fun resolveFlowByPathOrName(
        projectUid: String,
        pathOrName: String
    ): Either<AppException, FlowEntry> =
        either {
            val flows = loadFlows(projectUid = projectUid).bind()

            val (groupNames, flowName) = resolveGroupsAndName(pathOrName)
                .mapLeft { exception -> AppException(cause = exception) }
                .bind()

            if (groupNames.isNotEmpty()) {
                resolveGroups(
                    projectUid = projectUid,
                    groupNames = groupNames
                ).bind()
            }

            val candidates = flows
                .filter { flow ->
                    flow.projectUid == projectUid &&
                        flow.name.contentEquals(flowName, ignoreCase = true)
                }

            candidates.firstOrNull()
                ?: raise(FailedToFindEntityByNameException(FlowEntry::class, flowName))
        }

    private suspend fun resolveGroups(
        projectUid: String,
        groupNames: List<String>
    ): Either<AppException, List<GroupEntry>> =
        either {
            val groups = groupRepository.getGroupsByProjectUid(projectUid = projectUid).bind()
            val tree = groups.buildGroupTree()

            val matchedGroups = mutableListOf<GroupEntry>()

            var currentNodes: List<TreeNode> = if (tree.nodes.size == 1) {
                tree.nodes.first().nodes
            } else {
                tree.nodes
            }
            for (name in groupNames) {
                var isFound = false
                for (node in currentNodes) {
                    val group = node.entity as GroupEntry
                    if (group.name == name) {
                        isFound = true
                        matchedGroups.add(group)
                        currentNodes = node.nodes
                        break
                    }
                }

                if (!isFound) {
                    raise(AppException("Failed to resolve group by name: $name"))
                }
            }

            matchedGroups
        }

    private suspend fun loadFlows(projectUid: String): Either<AppException, List<FlowEntry>> =
        either {
            val allFlows = flowRepository.getFlowsByProjectUid(projectUid = projectUid).bind()
                .sortedByDescending { flow -> flow.id }

            val flows = if (projectUid == LOCAL_PROJECT_UID) {
                val nameToFlowMap = HashMap<String, FlowEntry>()

                for (flow in allFlows) {
                    if (flow.name !in nameToFlowMap) {
                        nameToFlowMap[flow.name] = flow
                    }
                }

                nameToFlowMap.values.toList()
            } else {
                allFlows
            }

            flows
        }

    suspend fun resolveProjectAndGroupByPath(
        projectName: String?,
        groupName: String?
    ): Either<AppException, Pair<ProjectEntry?, GroupEntry?>> =
        either {
            val projects = projectRepository.getProjects().bind()
            val groups = groupRepository.getGroups().bind()

            val project = if (projectName != null) {
                projects.firstOrNull { project -> project.name == projectName }
            } else {
                null
            }

            val group = if (groupName != null) {
                groups.firstOrNull { group ->
                    if (project != null) {
                        group.projectUid == project.uid && group.name == groupName
                    } else {
                        group.name == groupName
                    }
                }
            } else {
                null
            }

            project to group
        }
}