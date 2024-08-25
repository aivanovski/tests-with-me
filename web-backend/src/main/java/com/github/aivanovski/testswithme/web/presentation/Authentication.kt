package com.github.aivanovski.testswithme.web.presentation

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.aivanovski.testswithme.web.api.response.ErrorMessage
import com.github.aivanovski.testswithme.web.entity.JwtData
import com.github.aivanovski.testswithme.web.presentation.Errors.INVALID_OR_EXPIRED_TOKEN
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

const val AUTH_PROVIDER = "jwt-auth-provider"

fun Application.configureAuthentication() {
    // TODO: should be read from properties file in production environment
    val jwtData = JwtData.DEFAULT

    install(Authentication) {
        jwt(AUTH_PROVIDER) {
            realm = jwtData.realm

            verifier(
                JWT.require(Algorithm.HMAC256(jwtData.secret))
                    .withAudience(jwtData.audience)
                    .withIssuer(jwtData.issuer)
                    .build()
            )

            validate { credential ->
                JWTPrincipal(credential.payload)
            }

            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = ErrorMessage(INVALID_OR_EXPIRED_TOKEN)
                )
            }
        }
    }
}