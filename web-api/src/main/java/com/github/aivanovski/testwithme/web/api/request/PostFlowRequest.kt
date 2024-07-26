package com.github.aivanovski.testwithme.web.api.request

import kotlinx.serialization.Serializable

@Serializable
data class PostFlowRequest(
    val projectId: String?,
    val groupId: String?,
    val path: String?,
    val base64Content: String
)