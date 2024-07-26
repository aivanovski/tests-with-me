package com.github.aivanovski.testwithme.android.presentation.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.aivanovski.testwithme.android.R
import com.github.aivanovski.testwithme.android.entity.ErrorMessage
import com.github.aivanovski.testwithme.android.presentation.core.compose.AppTextField
import com.github.aivanovski.testwithme.android.presentation.core.compose.ErrorMessage
import com.github.aivanovski.testwithme.android.presentation.core.compose.ProgressIndicator
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.screens.login.model.LoginIntent
import com.github.aivanovski.testwithme.android.presentation.screens.login.model.LoginState

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
    when (state) {
        LoginState.NotInitialized -> {}

        LoginState.Loading -> {
            ProgressIndicator()
        }

        is LoginState.Data -> {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(top = GroupMargin)
                        .fillMaxWidth(fraction = 0.8f)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (state.errorMessage != null) {
                        ErrorMessage(
                            message = state.errorMessage
                        )
                    }

                    AppTextField(
                        value = state.username,
                        label = stringResource(R.string.username),
                        onValueChange = { newUsername ->
                            onIntent.invoke(LoginIntent.OnUsernameChanged(newUsername))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    AppTextField(
                        value = state.password,
                        label = stringResource(R.string.password),
                        onValueChange = { newPassword ->
                            onIntent.invoke(LoginIntent.OnPasswordChanged(newPassword))
                        },
                        isPasswordToggleEnabled = true,
                        isPasswordVisible = state.isPasswordVisible,
                        onPasswordToggleClicked = { isPasswordVisible ->
                            onIntent.invoke(
                                LoginIntent.OnPasswordVisibilityChanged(
                                    isPasswordVisible
                                )
                            )
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    )

                    Button(
                        onClick = { // TODO: optimize
                            onIntent.invoke(LoginIntent.OnLoginButtonClicked)
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.login)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun DataStatePreview() {
    ThemedScreenPreview(theme = LightTheme) {
        LoginScreen(
            state = newDataState(),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun ErrorStatePreview() {
    ThemedScreenPreview(theme = LightTheme) {
        LoginScreen(
            state = newDataWithErrorState(),
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

private fun newLoadingState(): LoginState.Loading = LoginState.Loading

private fun newDataState(): LoginState.Data =
    LoginState.Data(
        username = "john.doe",
        password = "abc123",
        isPasswordVisible = false,
        errorMessage = null
    )

@Composable
private fun newDataWithErrorState(): LoginState.Data =
    LoginState.Data(
        username = "john.doe",
        password = "abc123",
        isPasswordVisible = false,
        errorMessage = ErrorMessage(
            message = stringResource(R.string.error_has_been_occurred),
            cause = Exception()
        )
    )