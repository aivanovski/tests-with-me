package com.github.aivanovski.testwithme.web.api.response

import com.github.aivanovski.testwithme.web.api.FlowsItemDto
import kotlinx.serialization.Serializable

@Serializable
class FlowsResponse(
    val flows: List<FlowsItemDto>
)