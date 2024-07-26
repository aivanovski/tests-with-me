package com.github.aivanovski.testwithme.web.domain.service

import arrow.core.Either
import arrow.core.raise.either
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.github.aivanovski.testwithme.web.data.repository.UserRepository
import com.github.aivanovski.testwithme.web.entity.Credentials
import com.github.aivanovski.testwithme.web.entity.ErrorResponse
import com.github.aivanovski.testwithme.web.entity.JwtData
import com.github.aivanovski.testwithme.web.entity.User
import com.github.aivanovski.testwithme.web.entity.exception.ExpiredTokenException
import com.github.aivanovski.testwithme.web.entity.exception.InvalidTokenException
import com.github.aivanovski.testwithme.web.extensions.toErrorResponse
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory

class AuthService(
    private val userRepository: UserRepository
) {

    private val storage: MutableMap<Credentials, String> = ConcurrentHashMap<Credentials, String>()

    fun isCredentialsValid(credentials: Credentials): Boolean {
        val user = userRepository.getUserByName(credentials.username).getOrNull()
            ?: return false

        return credentials.username == user.name && credentials.password == user.password
    }

    fun validateToken(principal: JWTPrincipal): Either<ErrorResponse, User> = either {
        val username = principal.payload.getClaim(USERNAME).asString()
        val expiresAt = principal.expiresAt?.time

        logger.debug(
            "validateToken: username={}, expiresAt={}",
            username,
            expiresAt?.let { Date(it) }
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

    fun getOrCreateToken(
        credentials: Credentials
    ): String {
        val existingToken = storage[credentials]
        if (existingToken != null) {
            val token = JWT.decode(existingToken)
            if (!token.isExpired()) {
                logger.debug("Reuse existing token: token={}", existingToken)
                return existingToken
            }
        }

        val newToken = createToken(credentials.username)
        storage[credentials] = newToken

        logger.debug("Created new token: username={}, token={}", credentials, newToken)

        return newToken
    }

    private fun createToken(
        username: String
    ): String {
        val jwtData = JwtData.DEFAULT

        // TODO: expiration was prolonged for developing needs
        // val expires = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
        val expires = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(60)

        return JWT.create()
            .withAudience(jwtData.audience)
            .withIssuer(jwtData.issuer)
            .withClaim(USERNAME, username)
            .withExpiresAt(Date(expires))
            .sign(Algorithm.HMAC256(jwtData.secret))
    }

    private fun DecodedJWT.isExpired(): Boolean {
        return System.currentTimeMillis() >= expiresAt.time
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)

        private const val USERNAME = "username"
    }
}