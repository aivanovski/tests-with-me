package com.github.aivanovski.testswithme.android.presentation.screens.login

import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.extensions.asFlow
import com.github.aivanovski.testswithme.android.presentation.core.MviViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.UnshapedChipRowCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.login.cells.LoginCellFactory
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
import com.github.aivanovski.testswithme.utils.mutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

class LoginViewModel(
    private val interactor: LoginInteractor,
    private val resourceProvider: ResourceProvider,
    private val cellFactory: LoginCellFactory,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: LoginScreenArgs
) : MviViewModel<LoginState, LoginIntent>(
    initialState = LoginState(terminalState = TerminalState.Loading),
    initialIntent = LoginIntent.Initialize
) {

    private var debugCredentials by mutableStateFlow(emptyList<Pair<String, String>>())

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))
        rootViewModel.sendIntent(SetBottomBarState(BottomBarState.HIDDEN))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))
    }

    override fun handleIntent(intent: LoginIntent): Flow<LoginState> {
        return when (intent) {
            is LoginIntent.Initialize -> createInitialState().asFlow()
            is LoginIntent.OnLoginButtonClicked -> onLoginButtonClicked(state.value)
            is LoginIntent.OnUsernameChanged -> onUsernameChanged(intent)
            is LoginIntent.OnEmailChanged -> onEmailChanged(intent)
            is LoginIntent.OnPasswordChanged -> onPasswordChanged(intent)
            is LoginIntent.OnConfirmPasswordChanged -> onConfirmPasswordChanged(intent)
            is LoginIntent.OnPasswordVisibilityChanged -> onPasswordVisibilityChanged(intent)
            is LoginIntent.OnCreateButtonClicked -> onCreateButtonClicked(state.value)

            is LoginIntent.OnConfirmPasswordVisibilityChanged -> {
                onConfirmPasswordVisibilityChanged(intent)
            }

            is LoginIntent.OnJoinButtonClicked -> {
                navigateToJoinScreen()
                emptyFlow()
            }
        }
    }

    override fun handleCellIntent(intent: BaseCellIntent) {
        when (intent) {
            is UnshapedChipRowCellIntent.OnClick -> onDefaultUserClicked(intent.chipIndex)
        }
    }

    private fun onDefaultUserClicked(index: Int) {
        val (user, password) = debugCredentials.getOrNull(index) ?: return

        state.value = state.value.copy(
            username = user,
            password = password
        )
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
        debugCredentials = interactor.getDebugCredentials()

        val users = debugCredentials.map { (user, _) -> user }

        return LoginState(
            type = args.type,
            users = cellFactory.createUsersCell(
                users = users,
                intentProvider = intentProvider
            )
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