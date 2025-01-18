package com.github.aivanovski.testswithme.android.domain.gatewayServer.controllers

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.domain.gatewayServer.GatewayServer
import com.github.aivanovski.testswithme.android.domain.gatewayServer.dataConverters.convert
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerInteractor
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.ErrorMessage
import com.github.aivanovski.testswithme.android.gatewayServerApi.request.StartTestRequest
import com.github.aivanovski.testswithme.android.gatewayServerApi.response.StartTestResponse
import com.github.aivanovski.testswithme.android.entity.OnFinishAction
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.GatewayException
import com.github.aivanovski.testswithme.android.entity.exception.InvalidBase64StringException
import com.github.aivanovski.testswithme.android.entity.exception.ParsingException
import com.github.aivanovski.testswithme.extensions.getRootCause
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.utils.Base64Utils
import com.github.aivanovski.testswithme.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class StartTestController(
    private val flowRunnerInteractor: FlowRunnerInteractor
) {

    suspend fun startTest(request: StartTestRequest): Either<GatewayException, StartTestResponse> =
        withContext(Dispatchers.IO) {
            either {
                val processResult = processRequest(request)

                if (processResult.isRight()) {
                    StartTestResponse(
                        isStarted = true,
                        jobId = processResult.unwrap(),
                        error = null
                    )
                } else {
                    val cause = processResult.unwrapError()
                    val rootCause = cause.getRootCause()

                    val message = when {
                        rootCause.message != null -> rootCause.message
                        else -> rootCause::class.simpleName
                    } ?: StringUtils.EMPTY

                    Timber.tag(GatewayServer::class.java.simpleName).d(cause)

                    StartTestResponse(
                        isStarted = false,
                        jobId = null,
                        error = ErrorMessage(
                            base64Message = Base64Utils.encode(message)
                        )
                    )
                }
            }
        }

    private suspend fun processRequest(request: StartTestRequest): Either<AppException, String> =
        either {
            val base64Content = request.base64Content

            val content = Base64Utils.decode(base64Content)
                .mapLeft { exception -> ParsingException(cause = exception) }
                .bind()
                ?: raise(InvalidBase64StringException())

            val flow = flowRunnerInteractor.parseFlow(
                base64Content = base64Content,
                name = request.name,
                contentHash = request.contentHash.convert()
            ).bind()

            flowRunnerInteractor.saveFlowContent(
                flowUid = flow.entry.uid,
                content = content
            ).bind()

            flowRunnerInteractor.removeAllJobs().bind()

            flowRunnerInteractor.addFlowToJobQueue(
                flow = flow,
                onFinishAction = OnFinishAction.STOP
            ).bind()
        }
}