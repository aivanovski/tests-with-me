package com.github.aivanovski.testswithme.android.gatewayServerApi.response

import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.ErrorMessage
import kotlinx.serialization.Serializable

@Serializable
data class StartTestResponse(
    val isStarted: Boolean,
    val jobId: String?,
    val error: ErrorMessage?
)