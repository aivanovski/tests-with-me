package com.github.aivanovski.testswithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class FlowsItemDto(
    val id: String,
    val projectId: String,
    val groupId: String?,
    val name: String
)