package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.extensions.orNull
import com.github.aivanovski.testswithme.web.api.request.PostProjectRequest
import com.github.aivanovski.testswithme.web.api.response.PostProjectResponse
import com.github.aivanovski.testswithme.web.api.response.ProjectsItemDto
import com.github.aivanovski.testswithme.web.api.response.ProjectsResponse
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EmptyRequestFieldException
import com.github.aivanovski.testswithme.web.entity.exception.EntityAlreadyExistsException

class ProjectController(
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository
) {

    fun postProject(
        user: User,
        request: PostProjectRequest
    ): Either<AppException, PostProjectResponse> =
        either {
            validateProjectData(request).bind()

            val projectUid = Uid.generate()
            val rootGroupUid = Uid.generate()

            val project = Project(
                uid = projectUid,
                userUid = user.uid,
                rootGroupUid = rootGroupUid,
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

    fun getProjects(user: User): Either<AppException, ProjectsResponse> =
        either {
            val projects = projectRepository.getByUserUid(user.uid).bind()

            val items = projects
                .map { project ->
                    ProjectsItemDto(
                        id = project.uid.toString(),
                        rootGroupId = project.rootGroupUid.toString(),
                        packageName = project.packageName,
                        name = project.name,
                        description = project.description,
                        downloadUrl = project.downloadUrl,
                        imageUrl = project.imageUrl,
                        siteUrl = project.siteUrl
                    )
                }

            ProjectsResponse(items)
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

    companion object {
        private const val FIELD_NAME = "name"
        private const val FIELD_PACKAGE_NAME = "packageName"
    }
}