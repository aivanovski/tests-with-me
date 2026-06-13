package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.dto.ProjectsItemDto
import kotlinx.serialization.Serializable

@Serializable
data class GetProjectsResponse(
    val projects: List<ProjectsItemDto>
)