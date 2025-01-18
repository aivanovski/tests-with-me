package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.di.GlobalInjector.inject
import com.github.aivanovski.testswithme.android.entity.Account
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.web.api.request.LoginRequest
import com.github.aivanovski.testswithme.web.api.request.SignUpRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AuthRepository(
    private val settings: Settings
) {

    private val api: ApiClient by inject()
    private val isLoggedIn = MutableStateFlow(settings.authToken != null)

    fun isUserLoggedIn(): Boolean = (settings.authToken != null)

    fun isLoggedInFlow(): Flow<Boolean> = isLoggedIn

    fun getAuthToken(): String? = settings.authToken

    fun getAccount(): Account? = settings.account

    suspend fun login(
        username: String,
        password: String
    ): Either<AppException, Unit> =
        either {
            val response = api.login(
                request = LoginRequest(
                    username = username,
                    password = password
                )
            ).bind()

            settings.authToken = response.token
            settings.account = Account(response.user.id, username, password)
            isLoggedIn.value = true
        }

    suspend fun createAccount(
        username: String,
        password: String,
        email: String
    ): Either<AppException, Unit> =
        either {
            val response = api.signUp(
                request = SignUpRequest(
                    username = username,
                    password = password,
                    email = email
                )
            ).bind()

            // TODO: additional login request could be avoided if sing-up will return user id
            val loginResponse = api.login(
                request = LoginRequest(
                    username = username,
                    password = password
                )
            ).bind()

            settings.authToken = response.token
            settings.account = Account(loginResponse.user.id, username, password)
            isLoggedIn.value = true
        }

    fun logout() {
        settings.account = null
        settings.authToken = null
        isLoggedIn.value = false
    }
}