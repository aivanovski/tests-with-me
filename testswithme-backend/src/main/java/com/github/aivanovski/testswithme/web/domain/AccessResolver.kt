package com.github.aivanovski.testswithme.web.domain

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidAccessException

class AccessResolver(
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository,
    private val flowRepository: FlowRepository
) {

    fun canReadFlow(
        user: User,
        flowUid: Uid
    ): Either<AppException, Unit> =
        either {
            val flow = flowRepository.getByUid(flowUid).bind()
            val project = projectRepository.getByUid(flow.projectUid).bind()

            if (project.userUid != user.uid) {
                raise(InvalidAccessException("Unable to access Flow with uid: $flowUid"))
            }
        }

    fun canModifyFlow(
        user: User,
        flowUid: Uid
    ): Either<AppException, Unit> =
        either {
            val flow = flowRepository.getByUid(flowUid).bind()
            val project = projectRepository.getByUid(flow.projectUid).bind()

            if (project.userUid != user.uid) {
                raise(InvalidAccessException("Unable to access Flow with uid: $flowUid"))
            }
        }

    fun canModifyGroup(
        user: User,
        groupUid: Uid
    ): Either<AppException, Unit> =
        either {
            val group = groupRepository.getByUid(groupUid).bind()
            val project = projectRepository.getByUid(group.projectUid).bind()

            if (project.userUid != user.uid) {
                raise(InvalidAccessException("Unable to access Group with uid: $groupUid"))
            }
        }

    fun canModifyProject(
        user: User,
        projectUid: Uid
    ): Either<AppException, Unit> =
        either {
            val project = projectRepository.getByUid(projectUid).bind()

            if (project.userUid != user.uid) {
                raise(InvalidAccessException("Unable to access Project with uid: $projectUid"))
            }
        }
}