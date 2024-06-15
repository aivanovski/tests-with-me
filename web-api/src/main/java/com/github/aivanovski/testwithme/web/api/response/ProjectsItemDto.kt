package com.github.aivanovski.testwithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ProjectsItemDto(
    val uid: String,
    val name: String
)