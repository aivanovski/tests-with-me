package com.github.aivanovski.testwithme.web.api.request

import kotlinx.serialization.Serializable

@Serializable
data class PostGroupRequest(
    val path: String?,
    val projectId: String?,
    val parentGroupId: String?,
    val name: String
)