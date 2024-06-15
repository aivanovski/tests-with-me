package com.github.aivanovski.testwithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ProjectsResponse(
    val projects: List<ProjectsItemDto>
)