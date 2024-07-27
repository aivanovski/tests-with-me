package com.github.aivanovski.testswithme.android.presentation.screens.login.model

sealed interface LoginIntent {

    object Initialize : LoginIntent

    object OnLoginButtonClicked : LoginIntent

    data class OnUsernameChanged(
        val username: String
    ) : LoginIntent

    data class OnPasswordChanged(
        val password: String
    ) : LoginIntent

    data class OnPasswordVisibilityChanged(
        val isVisible: Boolean
    ) : LoginIntent
}