package com.github.aivanovski.testswithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class PostProjectResponse(
    val id: String,
    val rootGroupId: String
)