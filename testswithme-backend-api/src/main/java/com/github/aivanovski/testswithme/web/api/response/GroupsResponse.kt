package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.GroupItemDto
import kotlinx.serialization.Serializable

@Serializable
data class GroupsResponse(
    val groups: List<GroupItemDto>
)