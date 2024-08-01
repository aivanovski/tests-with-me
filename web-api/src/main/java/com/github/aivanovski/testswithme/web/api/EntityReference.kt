package com.github.aivanovski.testswithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class EntityReference(
    val path: String?,
    val groupId: String?,
    val projectId: String?
)