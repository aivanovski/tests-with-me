package com.github.aivanovski.testswithme.android.presentation.screens.settings

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.driverServerApi.DriverServerEndpoints
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.BottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetBottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val interactor: SettingsInteractor,
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
        rootViewModel.sendIntent(SetBottomBarState(BottomBarState.HIDDEN))

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
                intent.isChecked
            )

            is SettingsIntent.OnHttpServerStateChanged -> onHttpServerStateChanged(
                state,
                intent.isChecked
            )
        }
    }

    private fun loadData(): Flow<SettingsState> {
        return flowOf(
            SettingsState(
                isLoading = false,
                isSslValidationChecked = !settings.isSslVerificationDisabled,
                isGatewayChecked = interactor.isGatewayRunning(),
                isGatewaySwitchEnabled = true,
                gatewayDescription = formatGatewayDescription(interactor.isGatewayRunning())
            )
        )
    }

    private fun onSslValidationStateChanged(
        state: SettingsState,
        isSslValidationChecked: Boolean
    ): Flow<SettingsState> {
        settings.isSslVerificationDisabled = !isSslValidationChecked

        return flowOf(
            state.copy(
                isSslValidationChecked = isSslValidationChecked
            )
        )
    }

    private fun onHttpServerStateChanged(
        initialState: SettingsState,
        isHttpServerChecked: Boolean
    ): Flow<SettingsState> {
        return flow {
            emit(
                initialState.copy(
                    isGatewayChecked = isHttpServerChecked,
                    isGatewaySwitchEnabled = false,
                    gatewayDescription = formatGatewayDescription(isHttpServerChecked)
                )
            )

            if (isHttpServerChecked != interactor.isGatewayRunning()) {
                if (isHttpServerChecked) {
                    interactor.startGatewayServer()
                } else {
                    interactor.stopGatewayServer()
                }
            }

            emit(
                initialState.copy(
                    isGatewayChecked = isHttpServerChecked,
                    isGatewaySwitchEnabled = true,
                    gatewayDescription = formatGatewayDescription(isHttpServerChecked)
                )
            )
        }
    }

    private fun formatGatewayDescription(isGatewayRunning: Boolean): String {
        val port = DriverServerEndpoints.PORT

        val status = if (isGatewayRunning) {
            resourceProvider.getString(R.string.running_on_port, port.toString())
        } else {
            resourceProvider.getString(R.string.stopped)
        }

        return resourceProvider.getString(R.string.driver_gateway_description, status)
    }

    private fun createLoadingState(): SettingsState =
        SettingsState(
            isLoading = true,
            isSslValidationChecked = false
        )

    private fun createTopBarState(): TopBarState =
        TopBarState(
            title = resourceProvider.getString(R.string.settings),
            isBackVisible = true
        )
}