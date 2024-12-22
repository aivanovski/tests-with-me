package com.github.aivanovski.testswithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class DeleteFlowResponse(
    val isSuccess: Boolean
)