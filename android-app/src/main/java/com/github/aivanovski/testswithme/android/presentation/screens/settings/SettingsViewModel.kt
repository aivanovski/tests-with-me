package com.github.aivanovski.testswithme.android.presentation.screens.settings

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settings: Settings,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router
) : BaseViewModel() {

    val state = MutableStateFlow(createLoadingState())

    private var isSubscribed = false
    private val intents = Channel<SettingsIntent>()

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))

        if (!isSubscribed) {
            isSubscribed = true

            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(SettingsIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(state.value, intent) }
                    .collect { newState ->
                        state.value = newState
                    }
            }
        }
    }

    fun sendIntent(intent: SettingsIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(
        state: SettingsState,
        intent: SettingsIntent
    ): Flow<SettingsState> {
        return when (intent) {
            is SettingsIntent.Initialize -> loadData()
            is SettingsIntent.OnSslValidationStateChanged -> onSslValidationStateChanged(
                state,
                intent.isEnabled
            )
        }
    }

    private fun loadData(): Flow<SettingsState> {
        return flowOf(
            SettingsState(
                isLoading = false,
                isSslValidationEnabled = !settings.isSslVerificationDisabled
            )
        )
    }

    private fun onSslValidationStateChanged(
        state: SettingsState,
        isSslValidationEnabled: Boolean
    ): Flow<SettingsState> {
        settings.isSslVerificationDisabled = !isSslValidationEnabled

        return flowOf(
            state.copy(
                isSslValidationEnabled = isSslValidationEnabled
            )
        )
    }

    private fun createLoadingState(): SettingsState =
        SettingsState(
            isLoading = true,
            isSslValidationEnabled = false
        )

    private fun createTopBarState(): TopBarState =
        TopBarState(
            title = resourceProvider.getString(R.string.settings),
            isBackVisible = true
        )
}