package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.dto.UserItemDto
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    val users: List<UserItemDto>
)