package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.dto.FlowsItemDto
import kotlinx.serialization.Serializable

@Serializable
data class FlowsResponse(
    val flows: List<FlowsItemDto>
)