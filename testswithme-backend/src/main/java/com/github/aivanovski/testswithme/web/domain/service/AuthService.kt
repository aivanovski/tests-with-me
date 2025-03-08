package com.github.aivanovski.testswithme.web.domain.service

import arrow.core.Either
import arrow.core.raise.either
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.aivanovski.testswithme.web.data.repository.UserRepository
import com.github.aivanovski.testswithme.web.domain.usecases.GetJwtDataUseCase
import com.github.aivanovski.testswithme.web.entity.Credentials
import com.github.aivanovski.testswithme.web.entity.ErrorResponse
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.ExpiredTokenException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidTokenException
import com.github.aivanovski.testswithme.web.extensions.toErrorResponse
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date
import kotlin.time.Duration.Companion.hours
import org.slf4j.LoggerFactory

class AuthService(
    private val userRepository: UserRepository,
    getJwtDataUseCase: GetJwtDataUseCase
) {

    private val jwtDatResult = getJwtDataUseCase.getJwtData()

    fun areCredentialsValid(credentials: Credentials): Boolean {
        val user = userRepository.getUserByName(credentials.username).getOrNull()
            ?: return false

        return credentials.username == user.name && credentials.password == user.password
    }

    fun validateToken(principal: JWTPrincipal): Either<ErrorResponse, User> =
        either {
            val username = principal.payload.getClaim(USERNAME).asString()
            val expiresAt = principal.expiresAt?.time

            logger.debug(
                "validateToken: username=%s, expiresAt=%s".format(
                    username,
                    expiresAt?.let { Date(it) }
                )
            )

            if (expiresAt == null) {
                raise(InvalidTokenException().toErrorResponse())
            }

            if (System.currentTimeMillis() >= expiresAt) {
                raise(ExpiredTokenException().toErrorResponse())
            }

            val user = userRepository.getUserByName(username)
                .mapLeft { error -> error.toErrorResponse() }
                .bind()

            user
        }

    fun createNewToken(credentials: Credentials): Either<AppException, String> =
        either {
            val jwtData = jwtDatResult.bind()

            val expires = System.currentTimeMillis() + TOKEN_VALIDITY_PERIOD

            val token = JWT.create()
                .withAudience(jwtData.audience)
                .withIssuer(jwtData.issuer)
                .withClaim(USERNAME, credentials.username)
                .withExpiresAt(Date(expires))
                .sign(Algorithm.HMAC256(jwtData.secret))

            logger.debug("Created new token: username=%s, token=%s".format(credentials, token))

            token
        }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)
        private const val USERNAME = "username"

        val TOKEN_VALIDITY_PERIOD = 2.hours.inWholeMilliseconds
    }
}