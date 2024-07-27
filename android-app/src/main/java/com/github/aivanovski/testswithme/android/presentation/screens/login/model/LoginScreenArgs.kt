package com.github.aivanovski.testswithme.android.presentation.screens.login.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginScreenArgs(
    val mode: LoginScreenMode
)