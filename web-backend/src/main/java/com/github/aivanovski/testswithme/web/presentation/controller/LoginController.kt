package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.api.ApiHeaders.X_REQUEST_SET_COOKIE
import com.github.aivanovski.testswithme.web.api.request.LoginRequest
import com.github.aivanovski.testswithme.web.api.response.LoginResponse
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.entity.Credentials
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidCredentialsException
import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall

class LoginController(
    private val authService: AuthService
) {

    fun login(
        call: ApplicationCall,
        request: LoginRequest
    ): Either<AppException, LoginResponse> =
        either {
            val headers = call.request.headers

            val credentials = request.toCredentials()
            val isSetCookie = "true".equals(headers[X_REQUEST_SET_COOKIE], ignoreCase = true)

            if (!authService.isCredentialsValid(credentials)) {
                raise(InvalidCredentialsException())
            }

            val token = authService.getOrCreateToken(credentials)
            if (isSetCookie) {
                call.response.headers.append(HttpHeaders.SetCookie, "$TOKEN=$token;")
            }

            LoginResponse(token)
        }

    private fun LoginRequest.toCredentials(): Credentials {
        return Credentials(
            username = username,
            password = password
        )
    }

    companion object {
        private const val TOKEN = "token"
    }
}