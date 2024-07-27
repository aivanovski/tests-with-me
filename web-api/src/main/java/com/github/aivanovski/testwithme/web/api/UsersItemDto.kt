package com.github.aivanovski.testwithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class UsersItemDto(
    val id: String,
    val name: String
)