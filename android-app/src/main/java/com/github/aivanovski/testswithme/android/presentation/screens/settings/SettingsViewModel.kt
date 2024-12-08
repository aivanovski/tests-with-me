package com.github.aivanovski.testswithme.android.presentation.screens.settings

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.ScreenState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.BottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetBottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.SettingsCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.SettingsCellFactory.CellId
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.SwitchCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel.SwitchCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsState
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsUiEvent
import com.github.aivanovski.testswithme.android.utils.infiniteRepeatFlow
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val interactor: SettingsInteractor,
    private val cellFactory: SettingsCellFactory,
    private val settings: Settings,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router
) : BaseViewModel() {

    val state = MutableStateFlow(SettingsState(screenState = ScreenState.Loading))

    private val _events = Channel<SettingsUiEvent>(capacity = Channel.BUFFERED)
    val events: Flow<SettingsUiEvent> = _events.receiveAsFlow()

    private var isSubscribed = false
    private var isGatewaySwitchEnabled = true
    private var isDriverRunning = interactor.isDriverRunning()
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

            viewModelScope.launch {
                infiniteRepeatFlow(1000.milliseconds)
                    .collect {
                        if (isDriverStatusChanged()) {
                            sendIntent(SettingsIntent.ReloadData)
                        }
                    }
            }
        } else {
            if (isDriverStatusChanged()) {
                sendIntent(SettingsIntent.ReloadData)
            }
        }
    }

    override fun handleCellIntent(intent: BaseCellIntent) {
        when (intent) {
            is HeaderCellIntent.OnIconClick -> {
                _events.trySend(SettingsUiEvent.ShowAccessibilityServices)
            }

            is SwitchCellIntent.OnCheckChanged -> {
                onSwitchStateChanged(
                    cellId = intent.cellId,
                    isChecked = intent.isChecked
                )
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
            is SettingsIntent.ReloadData -> loadData()
        }
    }

    private fun loadData(): Flow<SettingsState> {
        return flow {
            isDriverRunning = interactor.isDriverRunning()

            val viewModels = cellFactory.createCellViewModels(
                settings = settings,
                isDriverRunning = interactor.isDriverRunning(),
                isGatewayRunning = interactor.isGatewayRunning(),
                isGatewaySwitchEnabled = isGatewaySwitchEnabled,
                intentProvider = intentProvider
            )

            emit(SettingsState(viewModels = viewModels))
        }
    }

    private fun onSwitchStateChanged(
        cellId: String,
        isChecked: Boolean
    ) {
        val cellViewModel = findCellViewModelById(cellId)
        when {
            cellId == CellId.GATEWAY_SWITCH && cellViewModel is SwitchCellViewModel -> {
                onGatewayStateChanged(
                    isGatewayRunning = isChecked,
                    cellViewModel = cellViewModel
                )
            }

            cellId == CellId.SSL_VALIDATION_SWITCH -> {
                onSslValidationStateChanged(
                    isSslValidationChecked = isChecked
                )
            }
        }
    }

    private fun onSslValidationStateChanged(isSslValidationChecked: Boolean) {
        interactor.setSslVerificationEnabled(
            isSslVerificationEnabled = isSslValidationChecked
        )
    }

    private fun onGatewayStateChanged(
        isGatewayRunning: Boolean,
        cellViewModel: SwitchCellViewModel
    ) {
        viewModelScope.launch {
            isGatewaySwitchEnabled = false

            val model = cellViewModel.observableModel.value

            cellViewModel.observableModel.value = model.copy(
                isChecked = isGatewayRunning,
                isEnabled = isGatewaySwitchEnabled
            )

            if (isGatewayRunning != interactor.isGatewayRunning()) {
                if (isGatewayRunning) {
                    interactor.startGatewayServer()
                } else {
                    interactor.stopGatewayServer()
                }
            }

            isGatewaySwitchEnabled = true
            cellViewModel.observableModel.value = model.copy(
                isEnabled = isGatewaySwitchEnabled
            )
        }
    }

    private fun findCellViewModelById(cellId: String): CellViewModel? {
        return state.value.viewModels
            .firstOrNull { vm -> vm.model.id == cellId }
    }

    private fun isDriverStatusChanged(): Boolean {
        return interactor.isDriverRunning() != isDriverRunning
    }

    private fun createTopBarState(): TopBarState =
        TopBarState(
            title = resourceProvider.getString(R.string.settings),
            isBackVisible = true
        )
}