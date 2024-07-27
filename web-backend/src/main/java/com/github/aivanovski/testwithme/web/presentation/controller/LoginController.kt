package com.github.aivanovski.testwithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.web.api.request.LoginRequest
import com.github.aivanovski.testwithme.web.api.response.LoginResponse
import com.github.aivanovski.testwithme.web.domain.service.AuthService
import com.github.aivanovski.testwithme.web.entity.Credentials
import com.github.aivanovski.testwithme.web.entity.exception.AppException
import com.github.aivanovski.testwithme.web.entity.exception.InvalidCredentialsException

class LoginController(
    private val authService: AuthService
) {

    fun login(request: LoginRequest): Either<AppException, LoginResponse> =
        either {
            val credentials = request.toCredentials()

            if (!authService.isCredentialsValid(credentials)) {
                raise(InvalidCredentialsException())
            }

            val token = authService.getOrCreateToken(credentials)

            LoginResponse(token)
        }

    private fun LoginRequest.toCredentials(): Credentials {
        return Credentials(
            username = username,
            password = password
        )
    }
}