package com.github.aivanovski.testswithme.web.api.request

import kotlinx.serialization.Serializable

@Serializable
data class ResetFlowRunsRequest(
    val projectId: String,
    val versionName: String
)