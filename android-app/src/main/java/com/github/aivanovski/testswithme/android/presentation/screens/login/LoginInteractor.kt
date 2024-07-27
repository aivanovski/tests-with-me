package com.github.aivanovski.testswithme.android.presentation.screens.login

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginInteractor(
    private val api: ApiClient,
    private val settings: Settings
) {

    suspend fun login(
        username: String,
        password: String
    ): Either<AppException, Unit> =
        withContext(Dispatchers.IO) {
            either {
                val response = api.login(username, password).bind()
                settings.authToken = response.token
            }
        }
}