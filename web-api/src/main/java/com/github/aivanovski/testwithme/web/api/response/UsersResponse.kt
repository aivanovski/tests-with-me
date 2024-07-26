package com.github.aivanovski.testwithme.web.api.response

import com.github.aivanovski.testwithme.web.api.UsersItemDto
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    val users: List<UsersItemDto>
)