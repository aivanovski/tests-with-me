package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.GroupItemDto
import kotlinx.serialization.Serializable

@Serializable
data class UpdateGroupResponse(
    val group: GroupItemDto
)