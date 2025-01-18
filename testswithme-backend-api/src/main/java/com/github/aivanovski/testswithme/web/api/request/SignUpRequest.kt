package com.github.aivanovski.testswithme.web.api.request

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String
)