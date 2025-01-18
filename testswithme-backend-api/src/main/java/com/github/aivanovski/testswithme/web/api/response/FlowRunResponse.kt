package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.FlowRunItemDto
import kotlinx.serialization.Serializable

@Serializable
data class FlowRunResponse(
    val flowRun: FlowRunItemDto
)