package com.github.aivanovski.testwithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class FlowItemDto(
    val id: String,
    val projectId: String,
    val groupId: String?,
    val name: String,
    val base64Content: String
)