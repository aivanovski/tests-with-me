package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.api.request.SignUpRequest
import com.github.aivanovski.testswithme.web.api.response.SignUpResponse
import com.github.aivanovski.testswithme.web.data.repository.UserRepository
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.domain.usecases.ValidateEmailUseCase
import com.github.aivanovski.testswithme.web.entity.Credentials
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EmptyRequestFieldException
import com.github.aivanovski.testswithme.web.entity.exception.EntityAlreadyExistsException

class SignUpController(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val validateEmailUseCase: ValidateEmailUseCase
) {

    fun createNewUser(request: SignUpRequest): Either<AppException, SignUpResponse> =
        either {
            validaData(request).bind()

            val user = User(
                uid = Uid.generate(),
                name = request.username.trim(),
                email = request.email.trim(),
                password = request.password.trim()
            )

            userRepository.add(user).bind()

            val token = authService.getOrCreateToken(
                credentials = Credentials(
                    username = user.name,
                    password = user.password
                )
            )

            SignUpResponse(
                userId = user.uid.toString(),
                token = token
            )
        }

    private fun validaData(request: SignUpRequest): Either<AppException, Unit> =
        either {
            val name = request.username.trim()
            val email = request.email.trim()
            val password = request.password.trim()

            if (name.isEmpty()) {
                raise(EmptyRequestFieldException(FIELD_USER_NAME))
            }
            if (email.isEmpty()) {
                raise(EmptyRequestFieldException(FIELD_EMAIL))
            }
            if (password.isEmpty()) {
                raise(EmptyRequestFieldException(FIELD_PASSWORD))
            }

            val sameNameUsers = userRepository.getUsers()
                .bind()
                .filter { user -> user.name.equals(name, ignoreCase = true) }
            if (sameNameUsers.isNotEmpty()) {
                raise(EntityAlreadyExistsException(name))
            }

            val sameEmailUsers = userRepository.getUsers()
                .bind()
                .filter { user -> user.email.equals(email, ignoreCase = true) }
            if (sameEmailUsers.isNotEmpty()) {
                raise(EntityAlreadyExistsException(email))
            }

            validateEmailUseCase.validateEmail(email).bind()
        }

    companion object {
        private const val FIELD_USER_NAME = "username"
        private const val FIELD_EMAIL = "email"
        private const val FIELD_PASSWORD = "password"
    }
}