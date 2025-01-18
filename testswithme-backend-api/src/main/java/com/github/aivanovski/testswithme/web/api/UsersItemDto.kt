package com.github.aivanovski.testswithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class UsersItemDto(
    val id: String,
    val name: String
)