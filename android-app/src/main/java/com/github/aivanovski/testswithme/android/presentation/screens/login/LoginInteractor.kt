package com.github.aivanovski.testswithme.android.presentation.screens.login

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.data.repository.AuthRepository
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.UserInputValidationException
import com.github.aivanovski.testswithme.domain.validation.ValidateEmailUseCase

class LoginInteractor(
    private val authRepository: AuthRepository,
    private val resourceProvider: ResourceProvider,
    private val validateEmailUseCase: ValidateEmailUseCase
) {

    suspend fun login(
        username: String,
        password: String
    ): Either<AppException, Unit> = authRepository.login(username, password)

    fun validateAccountData(
        username: String,
        password: String,
        confirmPassword: String,
        email: String
    ): Either<AppException, Unit> =
        either {
            val errorMessage = when {
                username.isBlank() -> {
                    resourceProvider.getString(R.string.empty_username_message)
                }

                password.isBlank() -> {
                    resourceProvider.getString(R.string.empty_password_message)
                }

                confirmPassword.isBlank() -> {
                    resourceProvider.getString(R.string.empty_password_confirmation_message)
                }

                email.isBlank() -> {
                    resourceProvider.getString(R.string.empty_email_message)
                }

                password.trim() != confirmPassword.trim() -> {
                    resourceProvider.getString(R.string.password_do_not_match_message)
                }

                else -> null
            }

            if (errorMessage != null) {
                raise(UserInputValidationException(errorMessage))
            }

            validateEmailUseCase.validateEmail(email.trim())
                .mapLeft { exception ->
                    UserInputValidationException(
                        message = resourceProvider.getString(R.string.invalid_email_message),
                        cause = exception
                    )
                }
                .bind()
        }

    suspend fun createAccount(
        username: String,
        password: String,
        email: String
    ): Either<AppException, Unit> =
        authRepository.createAccount(
            username = username,
            password = password,
            email = email
        )
}