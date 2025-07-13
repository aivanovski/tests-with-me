package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.dto.FlowItemDto
import kotlinx.serialization.Serializable

@Serializable
data class UpdateFlowResponse(
    val flow: FlowItemDto
)