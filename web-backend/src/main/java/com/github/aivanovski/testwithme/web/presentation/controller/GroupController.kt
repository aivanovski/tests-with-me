package com.github.aivanovski.testwithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.web.api.GroupsItemDto
import com.github.aivanovski.testwithme.web.api.request.PostGroupRequest
import com.github.aivanovski.testwithme.web.api.response.GroupsResponse
import com.github.aivanovski.testwithme.web.api.response.PostGroupResponse
import com.github.aivanovski.testwithme.web.data.repository.GroupRepository
import com.github.aivanovski.testwithme.web.domain.usecases.ResolvePathUseCase
import com.github.aivanovski.testwithme.web.entity.Group
import com.github.aivanovski.testwithme.web.entity.Project
import com.github.aivanovski.testwithme.web.entity.Uid
import com.github.aivanovski.testwithme.web.entity.User
import com.github.aivanovski.testwithme.web.entity.exception.AppException
import com.github.aivanovski.testwithme.web.entity.exception.EmptyRequestFieldException
import com.github.aivanovski.testwithme.web.entity.exception.EntityAlreadyExistsException

class GroupController(
    private val groupRepository: GroupRepository,
    private val resolvePathUseCase: ResolvePathUseCase
) {

    fun postGroup(
        user: User,
        request: PostGroupRequest
    ): Either<AppException, PostGroupResponse> =
        either {
            val (project, parent) = resolvePathUseCase.resolveProjectAndGroup(
                path = request.path,
                projectUid = request.projectId,
                groupUid = request.parentGroupId,
                user = user
            ).bind()

            validateGroupName(
                name = request.name,
                project = project,
                parent = parent
            ).bind()

            val group = Group(
                uid = Uid.generate(),
                parentUid = parent?.uid,
                projectUid = project.uid,
                name = request.name
            )

            groupRepository.add(group).bind()

            PostGroupResponse(
                id = group.uid.toString()
            )
        }

    fun getGroups(user: User): Either<AppException, GroupsResponse> =
        either {
            val groups = groupRepository.getByUserUid(user.uid).bind()

            GroupsResponse(
                groups = groups.map { group ->
                    GroupsItemDto(
                        id = group.uid.toString(),
                        parentId = group.parentUid?.toString(),
                        projectId = group.projectUid.toString(),
                        name = group.name
                    )
                }
            )
        }

    private fun validateGroupName(
        name: String,
        project: Project,
        parent: Group?
    ): Either<AppException, Unit> =
        either {
            if (name.isBlank()) {
                raise(EmptyRequestFieldException(FIELD_NAME))
            }

            val groupsInParent = groupRepository.getByProjectUid(project.uid)
                .bind()
                .filter { group -> group.parentUid == parent?.uid }

            val hasTheSameName = groupsInParent.any { group -> group.name == name }
            if (hasTheSameName) {
                raise(EntityAlreadyExistsException(name))
            }
        }

    companion object {
        private const val FIELD_NAME = "name"
    }
}