package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.web.api.GroupItemDto
import com.github.aivanovski.testswithme.web.api.request.PostGroupRequest
import com.github.aivanovski.testswithme.web.api.request.UpdateGroupRequest
import com.github.aivanovski.testswithme.web.api.response.DeleteGroupResponse
import com.github.aivanovski.testswithme.web.api.response.GroupsResponse
import com.github.aivanovski.testswithme.web.api.response.PostGroupResponse
import com.github.aivanovski.testswithme.web.api.response.UpdateGroupResponse
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.domain.AccessResolver
import com.github.aivanovski.testswithme.web.domain.ReferenceResolver
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.BadRequestException
import com.github.aivanovski.testswithme.web.entity.exception.EmptyRequestFieldException
import com.github.aivanovski.testswithme.web.entity.exception.EntityAlreadyExistsException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidAccessException

class GroupController(
    private val groupRepository: GroupRepository,
    private val flowRepository: FlowRepository,
    private val referenceResolver: ReferenceResolver,
    private val accessResolver: AccessResolver
) {

    fun updateGroup(
        user: User,
        uid: String,
        request: UpdateGroupRequest
    ): Either<AppException, UpdateGroupResponse> =
        either {
            val groupUid = Uid.parse(uid).bind()

            if (request.parent == null && request.name.isNullOrBlank()) {
                raise(BadRequestException("Request is empty"))
            }

            accessResolver.canModifyGroup(user, groupUid).bind()

            val group = groupRepository.getByUid(groupUid).bind()
            val parentReference = request.parent
            val (projectUid, parentUid) = if (parentReference != null) {
                val (newProject, newParentGroup) = referenceResolver.parseProjectAndGroup(
                    reference = parentReference,
                    user = user
                ).bind()

                newProject.uid to newParentGroup.uid
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
            val (project, parent) = referenceResolver.resolveProjectAndGroup(
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
                parentUid = parent.uid,
                projectUid = project.uid,
                name = request.name,
                isDeleted = false
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

    fun deleteGroup(
        user: User,
        uid: String
    ): Either<AppException, DeleteGroupResponse> =
        either {
            val groupUid = Uid.parse(uid).bind()

            accessResolver.canModifyGroup(user, groupUid).bind()

            val groupToDelete = groupRepository.getByUid(groupUid).bind()
            if (groupToDelete.parentUid == null) {
                raise(InvalidAccessException("Unable to delete root group"))
            }

            val childGroups = groupRepository.getChildGroups(groupToDelete.uid).bind()
            val groupsToDelete = (childGroups + groupToDelete)
            val groupUidsToDelete = groupsToDelete.map { group -> group.uid }
                .toSet()

            val projectFlows = flowRepository.getFlowsByProjectUid(groupToDelete.projectUid).bind()
            val flowsToDelete = projectFlows.filter { flow -> flow.groupUid in groupUidsToDelete }

            for (flow in flowsToDelete) {
                flowRepository.update(
                    flow.copy(
                        isDeleted = true
                    )
                )
            }

            for (group in groupsToDelete) {
                groupRepository.update(
                    group.copy(
                        isDeleted = true
                    )
                )
            }

            DeleteGroupResponse(
                isSuccess = true,
                modifiedGroupIds = groupsToDelete.map { group -> group.uid.toString() },
                modifiedFlowIds = flowsToDelete.map { flow -> flow.uid.toString() }
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