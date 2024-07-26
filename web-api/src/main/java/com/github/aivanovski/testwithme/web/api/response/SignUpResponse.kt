package com.github.aivanovski.testwithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val userId: String,
    val token: String
)