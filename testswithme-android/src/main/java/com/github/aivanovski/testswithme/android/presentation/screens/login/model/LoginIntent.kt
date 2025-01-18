package com.github.aivanovski.testswithme.android.presentation.screens.login.model

sealed interface LoginIntent {

    data object Initialize : LoginIntent

    data object OnLoginButtonClicked : LoginIntent

    data object OnJoinButtonClicked : LoginIntent

    data object OnCreateButtonClicked : LoginIntent

    data class OnUsernameChanged(
        val username: String
    ) : LoginIntent

    data class OnEmailChanged(
        val email: String
    ) : LoginIntent

    data class OnPasswordChanged(
        val password: String
    ) : LoginIntent

    data class OnConfirmPasswordChanged(
        val confirmPassword: String
    ) : LoginIntent

    data class OnPasswordVisibilityChanged(
        val isVisible: Boolean
    ) : LoginIntent

    data class OnConfirmPasswordVisibilityChanged(
        val isVisible: Boolean
    ) : LoginIntent
}