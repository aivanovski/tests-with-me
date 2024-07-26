package com.github.aivanovski.testwithme.web.api.response

import com.github.aivanovski.testwithme.web.api.FlowRunsItemDto
import kotlinx.serialization.Serializable

@Serializable
data class FlowRunsResponse(
    val stats: List<FlowRunsItemDto>
)