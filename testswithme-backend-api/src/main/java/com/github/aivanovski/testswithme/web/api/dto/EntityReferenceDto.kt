package com.github.aivanovski.testswithme.web.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EntityReferenceDto(
    val path: String?,
    val groupId: String?,
    val projectId: String?
)