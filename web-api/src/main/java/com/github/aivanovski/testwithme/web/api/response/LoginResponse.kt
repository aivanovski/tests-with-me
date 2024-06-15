package com.github.aivanovski.testwithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)