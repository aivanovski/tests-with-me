package com.github.aivanovski.testswithme.android.presentation.screens.login.model

import com.github.aivanovski.testswithme.android.entity.ErrorMessage

sealed interface LoginState {

    object NotInitialized : LoginState

    object Loading : LoginState

    data class Data(
        val username: String,
        val password: String,
        val isPasswordVisible: Boolean,
        val errorMessage: ErrorMessage?
    ) : LoginState
}