package com.github.aivanovski.testwithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.api.ApiClient
import com.github.aivanovski.testwithme.android.entity.FlowRun
import com.github.aivanovski.testwithme.android.entity.FlowRunUploadResult
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.web.api.request.PostFlowRunRequest

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
}