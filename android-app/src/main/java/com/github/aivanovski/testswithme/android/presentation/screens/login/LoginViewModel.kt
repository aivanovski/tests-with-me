package com.github.aivanovski.testswithme.android.presentation.screens.login

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.extensions.asFlow
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginIntent
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginState
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.BottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetBottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.utils.formatError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val interactor: LoginInteractor,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router
) : BaseViewModel() {

    val state = MutableStateFlow<LoginState>(LoginState.NotInitialized)

    private val intents = Channel<LoginIntent>()

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))
        rootViewModel.sendIntent(SetBottomBarState(BottomBarState.HIDDEN))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))

        if (state.value == LoginState.NotInitialized) {
            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(LoginIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent, state.value) }
                    .collect { newState ->
                        state.value = newState
                    }
            }
        }
    }

    fun sendIntent(intent: LoginIntent) {
        viewModelScope.launch {
            intents.send(intent)
        }
    }

    private fun handleIntent(
        intent: LoginIntent,
        state: LoginState
    ): Flow<LoginState> {
        return when (intent) {
            is LoginIntent.Initialize -> createInitialState().asFlow()
            is LoginIntent.OnLoginButtonClicked -> onLoginButtonClicked(state)
            is LoginIntent.OnUsernameChanged -> onUsernameChanged(intent, state).asFlow()
            is LoginIntent.OnPasswordChanged -> onPasswordChanged(intent, state).asFlow()
            is LoginIntent.OnPasswordVisibilityChanged -> onPasswordVisibilityChanged(
                intent = intent,
                currentState = state
            ).asFlow()
        }
    }

    private fun createInitialState(): LoginState {
        return LoginState.Data(
            username = "admin",
            password = "abc123",
            isPasswordVisible = false,
            errorMessage = null
        )
    }

    private fun onUsernameChanged(
        intent: LoginIntent.OnUsernameChanged,
        currentState: LoginState
    ): LoginState {
        val state = (currentState as? LoginState.Data) ?: return currentState

        return state.copy(
            username = intent.username
        )
    }

    private fun onPasswordChanged(
        intent: LoginIntent.OnPasswordChanged,
        currentState: LoginState
    ): LoginState {
        val state = (currentState as? LoginState.Data) ?: return currentState

        return state.copy(
            password = intent.password
        )
    }

    private fun onPasswordVisibilityChanged(
        intent: LoginIntent.OnPasswordVisibilityChanged,
        currentState: LoginState
    ): LoginState {
        val state = (currentState as? LoginState.Data) ?: return currentState

        return state.copy(
            isPasswordVisible = intent.isVisible
        )
    }

    private fun onLoginButtonClicked(currentState: LoginState): Flow<LoginState> {
        val state = (currentState as? LoginState.Data) ?: return flowOf(currentState)

        return flow {
            emit(LoginState.Loading)

            val response = interactor.login(state.username, state.password)

            if (response.isRight()) {
                router.setRoot(Screen.Projects)
            } else {
                emit(
                    currentState.copy(
                        errorMessage = response.formatError(resourceProvider)
                    )
                )
            }
        }
    }

    private fun createTopBarState(): TopBarState {
        return TopBarState(
            title = resourceProvider.getString(R.string.login),
            isBackVisible = false
        )
    }
}