package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.utils.StringUtils.SPACE
import com.github.aivanovski.testswithme.web.api.ApiHeaders.X_REQUEST_SET_COOKIE
import com.github.aivanovski.testswithme.web.api.request.LoginRequest
import com.github.aivanovski.testswithme.web.api.response.LoginResponse
import com.github.aivanovski.testswithme.web.data.repository.UserRepository
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.domain.service.AuthService.Companion.TOKEN_VALIDITY_PERIOD
import com.github.aivanovski.testswithme.web.entity.Credentials
import com.github.aivanovski.testswithme.web.entity.Response
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidCredentialsException
import com.github.aivanovski.testswithme.web.extensions.toDto
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class LoginController(
    private val authService: AuthService,
    private val userRepository: UserRepository
) {

    fun login(
        headers: Headers,
        request: LoginRequest
    ): Either<AppException, Response<LoginResponse>> =
        either {
            val credentials = request.toCredentials()
            val isSetCookie = "true".equals(headers[X_REQUEST_SET_COOKIE], ignoreCase = true)

            if (!authService.areCredentialsValid(credentials)) {
                raise(InvalidCredentialsException())
            }

            val token = authService.createNewToken(credentials).bind()
            val user = userRepository.getUserByName(name = credentials.username).bind()

            val responseHeaders = if (isSetCookie) {
                listOf(HttpHeaders.SetCookie to buildCookieWithToken(token))
            } else {
                emptyList()
            }

            Response(
                response = LoginResponse(
                    token = token,
                    user = user.toDto()
                ),
                headers = responseHeaders
            )
        }

    private fun buildCookieWithToken(token: String): String {
        return StringBuilder()
            .apply {
                append("token=$token;")
                append(SPACE).append("Path=/;")
                append(SPACE).append("Max-Age=$TOKEN_VALIDITY_PERIOD;")
                append(SPACE).append("SameSite=Lax;")
                append(SPACE).append("HttpOnly")
                // TODO: 'Secure' should be added to production setup
            }
            .toString()
    }

    private fun LoginRequest.toCredentials(): Credentials {
        return Credentials(
            username = username,
            password = password
        )
    }
}