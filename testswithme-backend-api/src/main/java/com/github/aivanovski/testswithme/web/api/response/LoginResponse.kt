package com.github.aivanovski.testswithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)