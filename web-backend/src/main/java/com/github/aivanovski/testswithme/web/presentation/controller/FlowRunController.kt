package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.utils.Base64Utils
import com.github.aivanovski.testswithme.web.api.FlowRunItemDto
import com.github.aivanovski.testswithme.web.api.FlowRunsItemDto
import com.github.aivanovski.testswithme.web.api.request.PostFlowRunRequest
import com.github.aivanovski.testswithme.web.api.request.ResetFlowRunsRequest
import com.github.aivanovski.testswithme.web.api.response.FlowRunResponse
import com.github.aivanovski.testswithme.web.api.response.FlowRunsResponse
import com.github.aivanovski.testswithme.web.api.response.PostFlowRunResponse
import com.github.aivanovski.testswithme.web.api.response.ResetFlowRunsResponse
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.domain.AccessResolver
import com.github.aivanovski.testswithme.web.entity.FlowRun
import com.github.aivanovski.testswithme.web.entity.Timestamp
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.BadRequestException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidBase64String
import com.github.aivanovski.testswithme.web.entity.exception.InvalidParameterException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidRequestFieldException
import com.github.aivanovski.testswithme.web.presentation.routes.Api.ID

class FlowRunController(
    private val flowRepository: FlowRepository,
    private val projectRepository: ProjectRepository,
    private val flowRunRepository: FlowRunRepository,
    private val accessResolver: AccessResolver
) {

    fun getFlowRuns(user: User): Either<AppException, FlowRunsResponse> =
        either {
            val userFlows = projectRepository.getByUserUid(user.uid).bind()
            val allStats = flowRunRepository.getAll().bind()

            val userFlowUids = userFlows.map { flow -> flow.uid }
            val filteredStats = allStats.filter { stat ->
                stat.userUid == user.uid || stat.flowUid in userFlowUids
            }

            val items = filteredStats.map { flowRun ->
                FlowRunsItemDto(
                    id = flowRun.uid.toString(),
                    flowId = flowRun.flowUid.toString(),
                    userId = flowRun.userUid.toString(),
                    finishedAt = flowRun.timestamp.formatForTransport(),
                    finishedAtTimestamp = flowRun.timestamp.milliseconds,
                    durationInMillis = flowRun.durationInMillis,
                    isSuccess = flowRun.isSuccess,
                    appVersionName = flowRun.appVersionName,
                    appVersionCode = flowRun.appVersionCode,
                    isExpired = flowRun.isExpired
                )
            }

            FlowRunsResponse(items)
        }

    fun getFlowRun(
        user: User,
        flowRunUid: String
    ): Either<AppException, FlowRunResponse> =
        either {
            val uid = Uid.parse(flowRunUid).getOrNull()
                ?: raise(InvalidParameterException(ID))

            val flowRun = flowRunRepository.findByUid(uid).bind()
                ?: raise(EntityNotFoundByUidException(FlowRun::class, uid))

            val reportContent = flowRunRepository.getReportContent(uid).bind()

            FlowRunResponse(
                flowRun = FlowRunItemDto(
                    id = flowRun.uid.toString(),
                    flowId = flowRun.flowUid.toString(),
                    userId = flowRun.userUid.toString(),
                    finishedAt = flowRun.timestamp.formatForTransport(),
                    finishedAtTimestamp = flowRun.timestamp.milliseconds,
                    durationInMillis = flowRun.durationInMillis,
                    isSuccess = flowRun.isSuccess,
                    appVersionName = flowRun.appVersionName,
                    appVersionCode = flowRun.appVersionCode,
                    isExpired = flowRun.isExpired,
                    reportBase64Content = Base64Utils.encode(reportContent)
                )
            )
        }

    fun postFlowRun(
        user: User,
        request: PostFlowRunRequest
    ): Either<AppException, PostFlowRunResponse> =
        either {
            val flowUid = Uid.parse(request.flowId).getOrNull()
                ?: raise(InvalidRequestFieldException(FIELD_FLOW_ID))

            val flow = flowRepository.getByUid(flowUid).bind()

            val report = Base64Utils.decode(request.reportBase64Content).getOrNull()
                ?: raise(InvalidBase64String())

            val uid = flowUid.append(Uid.generate())

            val reportPath = flowRunRepository.putReportContent(
                flowRunUid = uid,
                flowUid = flow.uid,
                content = report
            ).bind()

            val flowRun = FlowRun(
                uid = uid,
                flowUid = flow.uid,
                userUid = user.uid,
                timestamp = Timestamp.now(),
                isSuccess = request.isSuccess,
                durationInMillis = request.durationInMillis,
                result = request.result,
                appVersionName = request.appVersionName,
                appVersionCode = request.appVersionCode,
                reportPath = reportPath,
                isExpired = false
            )

            flowRunRepository.add(flowRun).bind()

            PostFlowRunResponse(
                id = flow.uid.toString(),
                isAccepted = true
            )
        }

    fun resetFlowRuns(
        user: User,
        request: ResetFlowRunsRequest,
    ): Either<AppException, ResetFlowRunsResponse> =
        either {
            val projectUid = Uid.parse(request.projectId).bind()
            val versionName = request.versionName.ifBlank {
                raise(BadRequestException("Invalid 'versionName' parameter: ${request.versionName}"))
            }

            accessResolver.canModifyProject(user, projectUid).bind()

            val flowRuns = flowRunRepository.getByProjectUid(projectUid).bind()
                .filter { flowRun ->
                    !flowRun.isExpired && flowRun.appVersionName.equals(
                        versionName,
                        ignoreCase = true
                    )
                }

            for (flowRun in flowRuns) {
                flowRunRepository.update(
                    flowRun.copy(
                        isExpired = true
                    )
                ).bind()
            }

            val modifiedIds = flowRuns.map { flowRun -> flowRun.flowUid.toString() }
            ResetFlowRunsResponse(
                isSuccess = true,
                modifiedIds = modifiedIds
            )
        }

    companion object {
        private const val FIELD_FLOW_ID = "flowId"
    }
}