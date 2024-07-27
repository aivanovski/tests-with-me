package com.github.aivanovski.testswithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val userId: String,
    val token: String
)