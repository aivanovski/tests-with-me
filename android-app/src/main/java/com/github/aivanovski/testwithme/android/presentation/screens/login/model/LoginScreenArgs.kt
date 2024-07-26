package com.github.aivanovski.testwithme.android.presentation.screens.login.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginScreenArgs(
    val mode: LoginScreenMode
)