package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.web.api.GroupItemDto
import com.github.aivanovski.testswithme.web.api.request.PostGroupRequest
import com.github.aivanovski.testswithme.web.api.request.UpdateGroupRequest
import com.github.aivanovski.testswithme.web.api.response.GroupsResponse
import com.github.aivanovski.testswithme.web.api.response.PostGroupResponse
import com.github.aivanovski.testswithme.web.api.response.UpdateGroupResponse
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.domain.PathResolver
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.BadRequestException
import com.github.aivanovski.testswithme.web.entity.exception.EmptyRequestFieldException
import com.github.aivanovski.testswithme.web.entity.exception.EntityAlreadyExistsException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidAccessException

class GroupController(
    private val groupRepository: GroupRepository,
    private val projectRepository: ProjectRepository,
    private val pathResolver: PathResolver
) {

    fun updateGroup(
        user: User,
        uid: String,
        request: UpdateGroupRequest
    ): Either<AppException, UpdateGroupResponse> =
        either {
            val groupUid = Uid.parse(uid).bind()
            val group = groupRepository.findByUid(groupUid).bind()
                ?: raise(EntityNotFoundByUidException(Group::class, groupUid))

            val project = projectRepository.findByUid(group.projectUid).bind()
                ?: raise(EntityNotFoundByUidException(Project::class, group.projectUid))

            if (project.userUid != user.uid) {
                raise(InvalidAccessException("Unable to access the group: uid=${group.uid}"))
            }

            if (request.parent == null && request.name.isNullOrBlank()) {
                raise(BadRequestException("Request is empty"))
            }

            val parentReference = request.parent
            val (projectUid, parentUid) = if (parentReference != null) {
                val (newProject, newParentGroup) = pathResolver.parseProjectAndGroup(
                    reference = parentReference,
                    user = user
                ).bind()

                newProject.uid to newParentGroup?.uid
            } else {
                group.projectUid to group.parentUid
            }

            val name = if (!request.name.isNullOrBlank()) {
                request.name?.trim() ?: StringUtils.EMPTY
            } else {
                group.name
            }

            val newGroup = group.copy(
                projectUid = projectUid,
                parentUid = parentUid,
                name = name
            )

            groupRepository.update(newGroup).bind()

            UpdateGroupResponse(
                group = newGroup.toDto()
            )
        }

    fun addGroup(
        user: User,
        request: PostGroupRequest
    ): Either<AppException, PostGroupResponse> =
        either {
            val (project, parent) = pathResolver.resolveProjectAndGroup(
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
            val items = groups.map { group -> group.toDto() }

            GroupsResponse(groups = items)
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

    private fun Group.toDto(): GroupItemDto {
        return GroupItemDto(
            id = uid.toString(),
            parentId = parentUid?.toString(),
            projectId = projectUid.toString(),
            name = name
        )
    }

    companion object {
        private const val FIELD_NAME = "name"
    }
}