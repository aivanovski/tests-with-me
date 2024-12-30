package com.github.aivanovski.testswithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ResetFlowRunsResponse(
    val isSuccess: Boolean,
    val modifiedIds: List<String>
)