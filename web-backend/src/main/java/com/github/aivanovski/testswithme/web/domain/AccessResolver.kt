package com.github.aivanovski.testswithme.web.domain

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidAccessException

class AccessResolver(
    private val projectRepository: ProjectRepository,
    private val flowRepository: FlowRepository
) {

    fun canModifyFlow(
        user: User,
        flowUid: Uid
    ): Either<AppException, Unit> =
        either {
            val flow = flowRepository.findByFlowUid(flowUid).bind()
                ?: raise(EntityNotFoundByUidException(Flow::class, flowUid))

            val project = projectRepository.findByUid(flow.projectUid).bind()
                ?: raise(EntityNotFoundByUidException(Project::class, flow.projectUid))

            if (project.userUid != user.uid) {
                raise(InvalidAccessException("Unable to access Flow with uid: $flowUid"))
            }
        }
}