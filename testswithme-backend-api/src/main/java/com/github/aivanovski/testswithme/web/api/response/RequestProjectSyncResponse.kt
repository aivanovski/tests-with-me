package com.github.aivanovski.testswithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class RequestProjectSyncResponse(
    val isSuccess: Boolean
)