package com.github.aivanovski.testwithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class FlowItemDto(
    val uid: String,
    val projectUid: String,
    val name: String,
    val base64Content: String
)