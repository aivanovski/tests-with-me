package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.FlowRunUploadResult
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.web.api.request.PostFlowRunRequest
import com.github.aivanovski.testswithme.web.api.request.ResetFlowRunsRequest
import com.github.aivanovski.testswithme.web.api.response.ResetFlowRunsResponse

class FlowRunRepository(
    private val api: ApiClient
) {

    suspend fun getRuns(): Either<AppException, List<FlowRun>> =
        either {
            api.getFlowRuns().bind()
        }

    suspend fun uploadRun(run: PostFlowRunRequest): Either<AppException, FlowRunUploadResult> =
        either {
            val response = api.postFlowRun(run).bind()

            FlowRunUploadResult(
                uid = response.id,
                isAccepted = response.isAccepted
            )
        }

    suspend fun resetRuns(
        projectUid: String,
        versionName: String
    ): Either<AppException, ResetFlowRunsResponse> =
        either {
            val request = ResetFlowRunsRequest(
                projectId = projectUid,
                versionName = versionName
            )

            api.resetFlowRun(request).bind()
        }
}