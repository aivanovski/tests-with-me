package com.github.aivanovski.testswithme.android.presentation.screens.login

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.BuildConfig
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.extensions.asFlow
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginIntent
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginScreenType
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginState
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.BottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetBottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.utils.formatError
import com.github.aivanovski.testswithme.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val interactor: LoginInteractor,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: LoginScreenArgs
) : BaseViewModel() {

    val state = MutableStateFlow(createInitialState())
    private val intents = Channel<LoginIntent>()
    private var isSubscribed: Boolean = false

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))
        rootViewModel.sendIntent(SetBottomBarState(BottomBarState.HIDDEN))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))

        if (!isSubscribed) {
            isSubscribed = true

            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(LoginIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent, state.value) }
                    .flowOn(Dispatchers.IO)
                    .collect { newState ->
                        state.value = newState
                    }
            }
        }
    }

    fun sendIntent(intent: LoginIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(
        intent: LoginIntent,
        state: LoginState
    ): Flow<LoginState> {
        return when (intent) {
            is LoginIntent.Initialize -> createInitialState().asFlow()
            is LoginIntent.OnLoginButtonClicked -> onLoginButtonClicked(state)
            is LoginIntent.OnUsernameChanged -> onUsernameChanged(intent)
            is LoginIntent.OnEmailChanged -> onEmailChanged(intent)
            is LoginIntent.OnPasswordChanged -> onPasswordChanged(intent)
            is LoginIntent.OnConfirmPasswordChanged -> onConfirmPasswordChanged(intent)
            is LoginIntent.OnPasswordVisibilityChanged -> onPasswordVisibilityChanged(intent)
            is LoginIntent.OnCreateButtonClicked -> onCreateButtonClicked(state)

            is LoginIntent.OnConfirmPasswordVisibilityChanged -> {
                onConfirmPasswordVisibilityChanged(intent)
            }

            is LoginIntent.OnJoinButtonClicked -> {
                navigateToJoinScreen()
                emptyFlow()
            }
        }
    }

    private fun onUsernameChanged(intent: LoginIntent.OnUsernameChanged): Flow<LoginState> =
        state.value.copy(
            username = intent.username
        ).asFlow()

    private fun onEmailChanged(intent: LoginIntent.OnEmailChanged): Flow<LoginState> =
        state.value.copy(
            email = intent.email
        ).asFlow()

    private fun onPasswordChanged(intent: LoginIntent.OnPasswordChanged): Flow<LoginState> =
        state.value.copy(
            password = intent.password
        ).asFlow()

    private fun onConfirmPasswordChanged(
        intent: LoginIntent.OnConfirmPasswordChanged
    ): Flow<LoginState> =
        state.value.copy(
            confirmPassword = intent.confirmPassword
        ).asFlow()

    private fun onPasswordVisibilityChanged(
        intent: LoginIntent.OnPasswordVisibilityChanged
    ): Flow<LoginState> =
        state.value.copy(
            isPasswordVisible = intent.isVisible
        ).asFlow()

    private fun onConfirmPasswordVisibilityChanged(
        intent: LoginIntent.OnConfirmPasswordVisibilityChanged
    ): Flow<LoginState> =
        state.value.copy(
            isConfirmPasswordVisible = intent.isVisible
        ).asFlow()

    private fun navigateToJoinScreen() {
        router.navigateTo(
            Screen.Login(
                LoginScreenArgs(
                    type = LoginScreenType.SIGN_UP
                )
            )
        )
    }

    private fun onCreateButtonClicked(state: LoginState): Flow<LoginState> {
        return flow {
            emit(state.copy(terminalState = TerminalState.Loading))

            val username = state.username.trim()
            val password = state.password.trim()
            val confirmPassword = state.confirmPassword.trim()
            val email = state.email.trim()

            val validationResult = interactor.validateAccountData(
                username = username,
                password = password,
                confirmPassword = confirmPassword,
                email = email
            )
            if (validationResult.isLeft()) {
                return@flow emit(
                    state.copy(
                        errorMessage = validationResult.formatError(resourceProvider)
                    )
                )
            }

            val response = interactor.createAccount(
                username = username,
                password = password,
                email = email
            )

            if (response.isRight()) {
                router.setRoot(Screen.Projects)
            } else {
                emit(
                    state.copy(
                        terminalState = null,
                        errorMessage = response.formatError(resourceProvider)
                    )
                )
            }
        }
    }

    private fun onLoginButtonClicked(state: LoginState): Flow<LoginState> {
        return flow {
            emit(state.copy(terminalState = TerminalState.Loading))

            val response = interactor.login(state.username, state.password)

            if (response.isRight()) {
                router.setRoot(Screen.Projects)
            } else {
                emit(
                    state.copy(
                        terminalState = null,
                        errorMessage = response.formatError(resourceProvider)
                    )
                )
            }
        }
    }

    private fun createInitialState(): LoginState {
        val (username, password) = if (BuildConfig.DEBUG && args.type == LoginScreenType.LOG_IN) {
            DEBUG_USERNAME to DEBUG_PASSWORD
        } else {
            StringUtils.EMPTY to StringUtils.EMPTY
        }

        return LoginState(
            type = args.type,
            username = username,
            password = password
        )
    }

    private fun createTopBarState(): TopBarState {
        val titleResourceId = when (args.type) {
            LoginScreenType.LOG_IN -> R.string.log_in
            LoginScreenType.SIGN_UP -> R.string.create_an_account
        }

        return TopBarState(
            title = resourceProvider.getString(titleResourceId),
            isBackVisible = true
        )
    }

    companion object {
        private const val DEBUG_USERNAME = "admin"
        private const val DEBUG_PASSWORD = "abc123"
    }
}