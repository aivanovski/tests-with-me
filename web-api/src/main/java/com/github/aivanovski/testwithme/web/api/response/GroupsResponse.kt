package com.github.aivanovski.testwithme.web.api.response

import com.github.aivanovski.testwithme.web.api.GroupsItemDto
import kotlinx.serialization.Serializable

@Serializable
data class GroupsResponse(
    val groups: List<GroupsItemDto>
)