package com.github.aivanovski.testwithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class GroupsItemDto(
    val id: String,
    val parentId: String?,
    val projectId: String,
    val name: String
)