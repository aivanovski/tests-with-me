package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.extensions.orNull
import com.github.aivanovski.testswithme.web.api.dto.ProcessedSyncItemDto
import com.github.aivanovski.testswithme.web.api.dto.ProcessedSyncItemTypeDto
import com.github.aivanovski.testswithme.web.api.dto.SyncResultDto
import com.github.aivanovski.testswithme.web.api.request.PostProjectRequest
import com.github.aivanovski.testswithme.web.api.response.GetProjectsResponse
import com.github.aivanovski.testswithme.web.api.response.PostProjectResponse
import com.github.aivanovski.testswithme.web.api.response.ProjectsItemDto
import com.github.aivanovski.testswithme.web.api.response.RequestProjectSyncResponse
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.data.repository.SyncResultRepository
import com.github.aivanovski.testswithme.web.data.repository.TestSourceRepository
import com.github.aivanovski.testswithme.web.domain.AccessResolver
import com.github.aivanovski.testswithme.web.domain.usecases.ParseGithubRepositoryUrlUseCase
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.SyncItemType
import com.github.aivanovski.testswithme.web.entity.SyncResultWithItems
import com.github.aivanovski.testswithme.web.entity.TestSource
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.BadRequestException
import com.github.aivanovski.testswithme.web.entity.exception.EmptyRequestFieldException
import com.github.aivanovski.testswithme.web.entity.exception.EntityAlreadyExistsException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidParameterException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidRequestFieldException
import com.github.aivanovski.testswithme.web.presentation.routes.Api.ID

class ProjectController(
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository,
    private val testSourceRepository: TestSourceRepository,
    private val accessResolver: AccessResolver,
    private val syncResultRepository: SyncResultRepository,
    private val parseGithubUrlUseCase: ParseGithubRepositoryUrlUseCase
) {

    fun postProject(
        user: User,
        request: PostProjectRequest
    ): Either<AppException, PostProjectResponse> =
        either {
            validateProjectData(request).bind()

            val projectUid = Uid.generate()
            val rootGroupUid = Uid.generate()
            val testSource = if (!request.repositoryUrl.isNullOrEmpty()) {
                val url = request.repositoryUrl.orEmpty().trim()

                parseGithubUrlUseCase.parseRepositoryUrl(url)
                    .mapLeft { _ -> InvalidRequestFieldException(FIELD_REPOSITORY_URL) }
                    .bind()

                val testSource = TestSource(
                    uid = Uid.generate(),
                    repositoryUrl = url,
                    lastCheckTimestamp = null,
                    lastCommitHash = null,
                    isForceSyncFlag = true
                )

                testSourceRepository.add(testSource).bind()
            } else {
                null
            }

            val project = Project(
                uid = projectUid,
                userUid = user.uid,
                rootGroupUid = rootGroupUid,
                testSourceUid = testSource?.uid,
                packageName = request.packageName,
                name = request.name,
                description = request.description?.trim()?.orNull(),
                downloadUrl = request.downloadUrl.trim(),
                imageUrl = request.imageUrl?.trim()?.orNull(),
                siteUrl = request.siteUrl?.trim()?.orNull(),
                isDeleted = false
            )

            val rootGroup = Group(
                uid = rootGroupUid,
                parentUid = null,
                projectUid = projectUid,
                name = GroupRepository.ROOT_GROUP_NAME,
                isDeleted = false
            )

            projectRepository.add(project).bind()
            groupRepository.add(rootGroup).bind()

            PostProjectResponse(
                id = project.uid.toString(),
                rootGroupId = rootGroup.uid.toString()
            )
        }

    fun getProjects(user: User): Either<AppException, GetProjectsResponse> =
        either {
            val projects = projectRepository.getByUserUid(user.uid).bind()

            val items = projects
                .map { project ->
                    val testSource = if (project.testSourceUid != null) {
                        testSourceRepository.getByUid(project.testSourceUid).bind()
                    } else {
                        null
                    }

                    val lastSyncResult = if (testSource != null) {
                        syncResultRepository.getAllWithItems()
                            .bind()
                            .filter { sync -> sync.result.testSourceUid == testSource.uid }
                            .maxByOrNull { sync -> sync.result.endTimestamp.milliseconds }
                    } else {
                        null
                    }

                    ProjectsItemDto(
                        id = project.uid.toString(),
                        rootGroupId = project.rootGroupUid.toString(),
                        packageName = project.packageName,
                        name = project.name,
                        description = project.description,
                        downloadUrl = project.downloadUrl,
                        imageUrl = project.imageUrl,
                        siteUrl = project.siteUrl,
                        repositoryUrl = testSource?.repositoryUrl,
                        lastSyncResult = lastSyncResult?.toDto()
                    )
                }

            GetProjectsResponse(items)
        }

    fun requestSync(
        user: User,
        projectId: String
    ): Either<AppException, RequestProjectSyncResponse> =
        either {
            val projectUid = Uid.parse(projectId).getOrNull()
                ?: raise(InvalidParameterException(ID))

            accessResolver.canModifyProject(
                user = user,
                projectUid = projectUid
            ).bind()

            val project = projectRepository.getByUid(projectUid).bind()
            if (project.testSourceUid == null) {
                raise(BadRequestException("Project doesn't have linked github repository"))
            }

            val testSource = testSourceRepository.getByUid(project.testSourceUid).bind()

            testSourceRepository.update(
                testSource.copy(
                    isForceSyncFlag = true
                )
            ).bind()

            RequestProjectSyncResponse(
                isSuccess = !testSource.isForceSyncFlag
            )
        }

    private fun validateProjectData(request: PostProjectRequest): Either<AppException, Unit> =
        either {
            if (request.name.isBlank()) {
                raise(EmptyRequestFieldException(FIELD_NAME))
            }

            if (request.packageName.isBlank()) {
                raise(EmptyRequestFieldException(FIELD_PACKAGE_NAME))
            }

            val identicalProjects = projectRepository.getAll()
                .bind()
                .filter { project -> project.name == request.name }

            if (identicalProjects.isNotEmpty()) {
                raise(EntityAlreadyExistsException(request.name))
            }
        }

    private fun SyncResultWithItems.toDto(): SyncResultDto {
        return SyncResultDto(
            isSuccess = result.isSuccess,
            startedAt = result.startTimestamp.formatForTransport(),
            startedAtTimestamp = result.startTimestamp.milliseconds,
            finishedAt = result.endTimestamp.formatForTransport(),
            finishedAtTimestamp = result.endTimestamp.milliseconds,
            processedItems = items.map { item ->
                ProcessedSyncItemDto(
                    path = item.path,
                    entityId = item.entityUid?.toString(),
                    type = item.type.toDto(),
                    isSuccess = item.isSuccess
                )
            }
        )
    }

    private fun SyncItemType.toDto(): ProcessedSyncItemTypeDto =
        when (this) {
            SyncItemType.INSERT_GROUP -> ProcessedSyncItemTypeDto.INSERT_GROUP
            SyncItemType.INSERT_FLOW -> ProcessedSyncItemTypeDto.INSERT_FLOW
            SyncItemType.UPDATE_FLOW -> ProcessedSyncItemTypeDto.UPDATE_FLOW
        }

    companion object {
        private const val FIELD_NAME = "name"
        private const val FIELD_REPOSITORY_URL = "repositoryUrl"
        private const val FIELD_PACKAGE_NAME = "packageName"
    }
}