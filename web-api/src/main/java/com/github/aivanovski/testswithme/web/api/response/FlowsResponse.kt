package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.FlowsItemDto
import kotlinx.serialization.Serializable

@Serializable
class FlowsResponse(
    val flows: List<FlowsItemDto>
)