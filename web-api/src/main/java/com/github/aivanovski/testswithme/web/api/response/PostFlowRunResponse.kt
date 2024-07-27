package com.github.aivanovski.testswithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class PostFlowRunResponse(
    val id: String,
    val isAccepted: Boolean
)