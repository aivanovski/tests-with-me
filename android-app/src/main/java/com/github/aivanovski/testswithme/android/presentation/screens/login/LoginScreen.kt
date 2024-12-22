package com.github.aivanovski.testswithme.android.presentation.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.entity.ErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalScreenState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppTextField
import com.github.aivanovski.testswithme.android.presentation.core.compose.ErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.DoubleGroupMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.QuarterMargin
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginIntent
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginScreenType
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginState

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val state by viewModel.state.collectAsState()

    LoginScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun LoginScreen(
    state: LoginState,
    onIntent: (event: LoginIntent) -> Unit
) {
    if (state.terminalState != null) {
        TerminalScreenState(state = state.terminalState)
    } else {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(
                        top = GroupMargin,
                        start = GroupMargin,
                        end = GroupMargin
                    )
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                when (state.type) {
                    LoginScreenType.LOG_IN -> LoginState(
                        username = state.username,
                        password = state.password,
                        isPasswordVisible = state.isPasswordVisible,
                        errorMessage = state.errorMessage,
                        onIntent = onIntent
                    )

                    LoginScreenType.SIGN_UP -> SignUpState(
                        username = state.username,
                        email = state.email,
                        password = state.password,
                        confirmPassword = state.confirmPassword,
                        isPasswordVisible = state.isPasswordVisible,
                        isConfirmPasswordVisible = state.isConfirmPasswordVisible,
                        errorMessage = state.errorMessage,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginState(
    username: String,
    password: String,
    isPasswordVisible: Boolean,
    errorMessage: ErrorMessage?,
    onIntent: (event: LoginIntent) -> Unit
) {
    val onLoginButtonClick = rememberOnClickedCallback {
        onIntent.invoke(LoginIntent.OnLoginButtonClicked)
    }
    val onJoinButtonClick = rememberOnClickedCallback {
        onIntent.invoke(LoginIntent.OnJoinButtonClicked)
    }
    val onUsernameChange = rememberCallback { newUsername: String ->
        onIntent.invoke(LoginIntent.OnUsernameChanged(newUsername))
    }
    val onPasswordChanged = rememberCallback { newPassword: String ->
        onIntent.invoke(LoginIntent.OnPasswordChanged(newPassword))
    }
    val onPasswordToggleClick = rememberCallback { isVisible: Boolean ->
        onIntent.invoke(LoginIntent.OnPasswordVisibilityChanged(isVisible))
    }

    if (errorMessage != null) {
        ErrorMessage(
            message = errorMessage
        )
    }

    AppTextField(
        value = username,
        label = stringResource(R.string.username),
        onValueChange = onUsernameChange,
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = password,
        label = stringResource(R.string.password),
        onValueChange = onPasswordChanged,
        isPasswordToggleEnabled = true,
        isPasswordVisible = isPasswordVisible,
        onPasswordToggleClicked = onPasswordToggleClick,
        modifier = Modifier
            .padding(top = QuarterMargin)
            .fillMaxWidth()
    )

    Button(
        onClick = onLoginButtonClick,
        modifier = Modifier
            .padding(top = QuarterMargin)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.log_in)
        )
    }

    Text(
        text = stringResource(R.string.dont_have_an_account),
        style = AppTheme.theme.typography.bodySmall,
        modifier = Modifier
            .padding(top = DoubleGroupMargin)
    )

    Button(
        onClick = onJoinButtonClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.theme.colors.grayButton,
            contentColor = AppTheme.theme.colors.primary
        ),
        modifier = Modifier
            .padding(top = QuarterMargin)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(
                R.string.join,
                stringResource(R.string.app_name)
            )
        )
    }
}

@Composable
private fun SignUpState(
    username: String,
    email: String,
    password: String,
    confirmPassword: String,
    isPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    errorMessage: ErrorMessage?,
    onIntent: (event: LoginIntent) -> Unit
) {
    val onCreateButtonClick = rememberOnClickedCallback {
        onIntent.invoke(LoginIntent.OnCreateButtonClicked)
    }
    val onUsernameChange = rememberCallback { newUsername: String ->
        onIntent.invoke(LoginIntent.OnUsernameChanged(newUsername))
    }
    val onEmailChange = rememberCallback { newEmail: String ->
        onIntent.invoke(LoginIntent.OnEmailChanged(newEmail))
    }
    val onPasswordChange = rememberCallback { newPassword: String ->
        onIntent.invoke(LoginIntent.OnPasswordChanged(newPassword))
    }
    val onConfirmPasswordChange = rememberCallback { newConfirmPassword: String ->
        onIntent.invoke(LoginIntent.OnConfirmPasswordChanged(newConfirmPassword))
    }
    val onPasswordToggleClick = rememberCallback { isVisible: Boolean ->
        onIntent.invoke(LoginIntent.OnPasswordVisibilityChanged(isVisible))
    }
    val onConfirmPasswordToggleClick = rememberCallback { isVisible: Boolean ->
        onIntent.invoke(LoginIntent.OnConfirmPasswordVisibilityChanged(isVisible))
    }

    if (errorMessage != null) {
        ErrorMessage(
            message = errorMessage
        )
    }

    AppTextField(
        value = username,
        label = stringResource(R.string.username),
        onValueChange = onUsernameChange,
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = email,
        label = stringResource(R.string.email),
        onValueChange = onEmailChange,
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = password,
        label = stringResource(R.string.password),
        onValueChange = onPasswordChange,
        isPasswordToggleEnabled = true,
        isPasswordVisible = isPasswordVisible,
        onPasswordToggleClicked = onPasswordToggleClick,
        modifier = Modifier
            .padding(top = QuarterMargin)
            .fillMaxWidth()
    )

    AppTextField(
        value = confirmPassword,
        label = stringResource(R.string.confirm_password),
        onValueChange = onConfirmPasswordChange,
        isPasswordToggleEnabled = true,
        isPasswordVisible = isConfirmPasswordVisible,
        onPasswordToggleClicked = onConfirmPasswordToggleClick,
        modifier = Modifier
            .padding(top = QuarterMargin)
            .fillMaxWidth()
    )

    Button(
        onClick = onCreateButtonClick,
        modifier = Modifier
            .padding(top = QuarterMargin)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.create)
        )
    }
}

@Composable
@Preview
fun LoginStatePreview() {
    ThemedScreenPreview(theme = LightTheme) {
        LoginScreen(
            state = newLoginState(),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun LoginWithErrorPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        LoginScreen(
            state = newLoginWithErrorState(),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun LoadingStatePreview() {
    ThemedScreenPreview(theme = LightTheme) {
        LoginScreen(
            state = newLoadingState(),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun SignUpStatePreview() {
    ThemedScreenPreview(theme = LightTheme) {
        LoginScreen(
            state = newSignUpState(),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun SignUpWithErrorStatePreview() {
    ThemedScreenPreview(theme = LightTheme) {
        LoginScreen(
            state = newSignUpWithErrorState(),
            onIntent = {}
        )
    }
}

private fun newLoadingState() =
    LoginState(
        terminalState = TerminalState.Loading
    )

private fun newLoginState() =
    LoginState(
        username = "john.doe",
        password = "abc123",
        isPasswordVisible = false,
        errorMessage = null
    )

@Composable
private fun newLoginWithErrorState() =
    LoginState(
        username = "john.doe",
        password = "abc123",
        isPasswordVisible = false,
        errorMessage = ErrorMessage(
            message = stringResource(R.string.error_has_been_occurred),
            cause = null
        )
    )

private fun newSignUpState() =
    LoginState(
        type = LoginScreenType.SIGN_UP,
        username = "john.doe",
        email = "john.doe@mail.com",
        password = "abc123",
        confirmPassword = "abc123"
    )

@Composable
private fun newSignUpWithErrorState() =
    LoginState(
        type = LoginScreenType.SIGN_UP,
        username = "john.doe",
        email = "john.doe@mail.com",
        password = "abc123",
        confirmPassword = "abc123",
        errorMessage = ErrorMessage(
            message = stringResource(R.string.error_has_been_occurred),
            cause = null
        )
    )