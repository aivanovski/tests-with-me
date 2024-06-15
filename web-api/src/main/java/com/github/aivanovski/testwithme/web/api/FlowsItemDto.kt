package com.github.aivanovski.testwithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class FlowsItemDto(
    val uid: String,
    val projectUid: String,
    val name: String
)