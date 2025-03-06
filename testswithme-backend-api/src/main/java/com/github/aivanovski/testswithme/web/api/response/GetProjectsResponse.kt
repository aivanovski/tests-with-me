package com.github.aivanovski.testswithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class GetProjectsResponse(
    val projects: List<ProjectsItemDto>
)