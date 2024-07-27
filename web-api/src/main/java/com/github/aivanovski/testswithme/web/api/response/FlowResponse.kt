package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.FlowItemDto
import kotlinx.serialization.Serializable

@Serializable
data class FlowResponse(
    val flow: FlowItemDto
)