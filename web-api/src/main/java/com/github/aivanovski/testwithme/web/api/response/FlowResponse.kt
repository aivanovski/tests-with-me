package com.github.aivanovski.testwithme.web.api.response

import com.github.aivanovski.testwithme.web.api.FlowItemDto
import kotlinx.serialization.Serializable

@Serializable
data class FlowResponse(
    val flow: FlowItemDto
)