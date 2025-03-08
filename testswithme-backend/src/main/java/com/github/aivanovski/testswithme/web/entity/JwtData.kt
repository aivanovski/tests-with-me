package com.github.aivanovski.testswithme.web.entity

data class JwtData(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
)