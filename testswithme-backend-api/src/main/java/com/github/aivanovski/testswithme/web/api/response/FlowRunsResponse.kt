package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.dto.FlowRunsItemDto
import kotlinx.serialization.Serializable

@Serializable
data class FlowRunsResponse(
    val stats: List<FlowRunsItemDto>
)