package com.github.aivanovski.testswithme.android.driverServerApi.response

import com.github.aivanovski.testswithme.android.driverServerApi.dto.ErrorMessage
import kotlinx.serialization.Serializable

@Serializable
data class StartTestResponse(
    val isStarted: Boolean,
    val jobId: String?,
    val error: ErrorMessage?
)