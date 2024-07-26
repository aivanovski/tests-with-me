package com.github.aivanovski.testwithme.web.api.response

import com.github.aivanovski.testwithme.web.api.FlowRunItemDto
import kotlinx.serialization.Serializable

@Serializable
data class FlowRunResponse(
    val flowRun: FlowRunItemDto
)