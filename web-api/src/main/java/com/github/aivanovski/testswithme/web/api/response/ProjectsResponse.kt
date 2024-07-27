package com.github.aivanovski.testswithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ProjectsResponse(
    val projects: List<ProjectsItemDto>
)