package com.github.aivanovski.testswithme.android.presentation.screens.login.model

import com.github.aivanovski.testswithme.android.entity.ErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.utils.StringUtils

data class LoginState(
    val type: LoginScreenType = LoginScreenType.LOG_IN,
    val terminalState: TerminalState? = null,
    val username: String = StringUtils.EMPTY,
    val email: String = StringUtils.EMPTY,
    val password: String = StringUtils.EMPTY,
    val confirmPassword: String = StringUtils.EMPTY,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val errorMessage: ErrorMessage? = null
)