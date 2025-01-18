package com.github.aivanovski.testswithme.web.entity

data class JwtData(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
) {

    companion object {
        // TODO: data should be in resources
        val DEFAULT = JwtData(
            secret = "secret",
            issuer = "https://0.0.0.0:8443",
            audience = "http://0.0.0.0:8443",
            realm = "TestWithMe"
        )
    }
}