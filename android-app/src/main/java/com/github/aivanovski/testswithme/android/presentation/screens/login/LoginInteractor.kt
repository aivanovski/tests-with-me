package com.github.aivanovski.testswithme.android.presentation.screens.login

import arrow.core.Either
import com.github.aivanovski.testswithme.android.data.repository.AuthRepository
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginInteractor(
    private val authRepository: AuthRepository
) {

    suspend fun login(
        username: String,
        password: String
    ): Either<AppException, Unit> =
        withContext(Dispatchers.IO) {
            authRepository.login(username, password)
        }
}