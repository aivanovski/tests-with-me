package com.github.aivanovski.testswithme.web.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class FlowsItemDto(
    val id: String,
    val projectId: String,
    val groupId: String,
    val name: String,
    val contentHash: Sha256HashDto
)