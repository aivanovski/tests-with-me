package com.github.aivanovski.testswithme.web.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserItemDto(
    val id: String,
    val name: String
)