package com.github.aivanovski.testwithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.extensions.orNull
import com.github.aivanovski.testwithme.web.api.request.PostProjectRequest
import com.github.aivanovski.testwithme.web.api.response.PostProjectResponse
import com.github.aivanovski.testwithme.web.api.response.ProjectsItemDto
import com.github.aivanovski.testwithme.web.api.response.ProjectsResponse
import com.github.aivanovski.testwithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testwithme.web.entity.Project
import com.github.aivanovski.testwithme.web.entity.Uid
import com.github.aivanovski.testwithme.web.entity.User
import com.github.aivanovski.testwithme.web.entity.exception.AppException
import com.github.aivanovski.testwithme.web.entity.exception.EmptyRequestFieldException
import com.github.aivanovski.testwithme.web.entity.exception.EntityAlreadyExistsException

class ProjectController(
    private val projectRepository: ProjectRepository
) {

    fun postProject(
        user: User,
        request: PostProjectRequest
    ): Either<AppException, PostProjectResponse> =
        either {
            validateProjectData(request).bind()

            val project = Project(
                uid = Uid.generate(),
                userUid = user.uid,
                packageName = request.packageName,
                name = request.name,
                description = request.description?.trim()?.orNull(),
                downloadUrl = request.downloadUrl.trim(),
                imageUrl = request.imageUrl?.trim()?.orNull(),
                siteUrl = request.siteUrl?.trim()?.orNull()
            )

            projectRepository.add(project)

            PostProjectResponse(
                id = project.uid.toString()
            )
        }

    fun getProjects(user: User): Either<AppException, ProjectsResponse> =
        either {
            val projects = projectRepository.getByUserUid(user.uid).bind()

            val items = projects
                .map { project ->
                    ProjectsItemDto(
                        id = project.uid.toString(),
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