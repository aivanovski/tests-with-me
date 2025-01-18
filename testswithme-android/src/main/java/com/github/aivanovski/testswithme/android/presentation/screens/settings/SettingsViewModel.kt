package com.github.aivanovski.testswithme.android.presentation.screens.settings

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.data.api.ApiUrlFactory
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.OptionDialogState
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
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.TwoTextCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel.SwitchCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsState
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsUiEvent
import com.github.aivanovski.testswithme.android.utils.infiniteRepeatFlow
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
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

    val state = MutableStateFlow(SettingsState(terminalState = TerminalState.Loading))

    private val _events = Channel<SettingsUiEvent>(capacity = Channel.BUFFERED)
    val events: Flow<SettingsUiEvent> = _events.receiveAsFlow()

    private var isSubscribed = false

    private var isGatewaySwitchEnabled = MutableStateFlow(true)
    private var isDriverRunning = MutableStateFlow(interactor.isDriverRunning())
    private var isGatewayRunning = MutableStateFlow(interactor.isGatewayRunning())
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
                    .flatMapLatest { intent -> handleIntent(intent) }
                    .flowOn(Dispatchers.IO)
                    .collect { newState ->
                        state.value = newState
                    }
            }

            viewModelScope.launch {
                infiniteRepeatFlow(1000.milliseconds)
                    .collect {
                        if (isDataChanged()) {
                            sendIntent(SettingsIntent.ReloadData)
                        }
                    }
            }
        } else {
            if (isDataChanged()) {
                sendIntent(SettingsIntent.ReloadData)
            }
        }
    }

    override fun handleCellIntent(intent: BaseCellIntent) {
        when (intent) {
            is TwoTextCellIntent.OnClick -> {
                when (intent.cellId) {
                    CellId.SERVER_URL -> showServerUrlDialog()
                    CellId.DELAY_SCALE_FACTOR -> showDelayScaleFactorDialog()
                    CellId.NUMBER_OF_RETRIES -> showNumberOfRetriesDialog()
                    else -> throw IllegalStateException()
                }
            }

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

    private fun handleIntent(intent: SettingsIntent): Flow<SettingsState> {
        return when (intent) {
            is SettingsIntent.Initialize -> loadData()
            is SettingsIntent.ReloadData -> loadData()
            is SettingsIntent.OnDismissOptionDialog -> dismissOptionDialog()
            is SettingsIntent.OnOptionDialogClick -> handleDialogAction(intent.action)
        }
    }

    private fun loadData(): Flow<SettingsState> {
        return flow {
            isDriverRunning.value = interactor.isDriverRunning()
            isGatewayRunning.value = interactor.isGatewayRunning()

            val viewModels = cellFactory.createCellViewModels(
                settings = settings,
                isDriverRunning = isDriverRunning.value,
                isGatewayRunning = isGatewayRunning.value,
                isGatewaySwitchEnabled = isGatewaySwitchEnabled.value,
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
            isGatewaySwitchEnabled.value = false

            val model = cellViewModel.observableModel.value

            cellViewModel.observableModel.value = model.copy(
                isChecked = isGatewayRunning,
                isEnabled = isGatewaySwitchEnabled.value
            )

            if (isGatewayRunning != interactor.isGatewayRunning()) {
                if (isGatewayRunning) {
                    interactor.startGatewayServer()
                } else {
                    interactor.stopGatewayServer()
                }
            }

            isGatewaySwitchEnabled.value = true
            cellViewModel.observableModel.value = model.copy(
                isEnabled = isGatewaySwitchEnabled.value
            )
        }
    }

    private fun showServerUrlDialog() {
        showOptionDialog(
            options = listOf(
                ApiUrlFactory.PROD_URL to DialogAction(ACTION_PROD_URL_SELECTED),
                ApiUrlFactory.DEBUG_URL to DialogAction(ACTION_DEBUG_URL_SELECTED)
            )
        )
    }

    private fun showDelayScaleFactorDialog() {
        showOptionDialog(
            options = listOf(
                "1x" to DialogAction(ACTION_DELAY_SCALE_1X),
                "2x" to DialogAction(ACTION_DELAY_SCALE_2X),
                "3x" to DialogAction(ACTION_DELAY_SCALE_3X)
            )
        )
    }

    private fun showNumberOfRetriesDialog() {
        showOptionDialog(
            options = listOf(
                "3" to DialogAction(ACTION_NUMBER_OF_RETRIES_3),
                "4" to DialogAction(ACTION_NUMBER_OF_RETRIES_4),
                "5" to DialogAction(ACTION_NUMBER_OF_RETRIES_5),
                "6" to DialogAction(ACTION_NUMBER_OF_RETRIES_6)
            )
        )
    }

    private fun showOptionDialog(options: List<Pair<String, DialogAction>>) {
        state.value = state.value.copy(
            optionDialogState = OptionDialogState(
                options = options.map { optionPair -> optionPair.first },
                actions = options.map { optionPair -> optionPair.second }
            )
        )
    }

    private fun handleDialogAction(action: DialogAction): Flow<SettingsState> {
        return when (action.actionId) {
            // Server url option
            ACTION_PROD_URL_SELECTED -> onServerUrlSelected(ApiUrlFactory.PROD_URL)
            ACTION_DEBUG_URL_SELECTED -> onServerUrlSelected(ApiUrlFactory.DEBUG_URL)

            // Delay Scale Factor
            ACTION_DELAY_SCALE_1X -> onDelayScaleFactorSelected(1)
            ACTION_DELAY_SCALE_2X -> onDelayScaleFactorSelected(2)
            ACTION_DELAY_SCALE_3X -> onDelayScaleFactorSelected(3)

            // Number of Retries
            ACTION_NUMBER_OF_RETRIES_3 -> onNumberOfRetriesSelected(3)
            ACTION_NUMBER_OF_RETRIES_4 -> onNumberOfRetriesSelected(4)
            ACTION_NUMBER_OF_RETRIES_5 -> onNumberOfRetriesSelected(5)
            ACTION_NUMBER_OF_RETRIES_6 -> onNumberOfRetriesSelected(6)

            else -> dismissOptionDialog()
        }
    }

    private fun onServerUrlSelected(url: String): Flow<SettingsState> {
        return flow {
            emit(
                state.value.copy(
                    optionDialogState = null,
                    terminalState = TerminalState.Loading
                )
            )

            if (settings.serverUrl != url) {
                interactor.clearAccountRelatedData()
                settings.serverUrl = url
            }

            emitAll(loadData())
        }
    }

    private fun onDelayScaleFactorSelected(delayScaleFactor: Int): Flow<SettingsState> {
        settings.delayScaleFactor = delayScaleFactor
        return loadData()
    }

    private fun onNumberOfRetriesSelected(numberOfRetries: Int): Flow<SettingsState> {
        settings.numberOfRetries = numberOfRetries
        return loadData()
    }

    private fun dismissOptionDialog(): Flow<SettingsState> {
        return flowOf(
            state.value.copy(
                optionDialogState = null
            )
        )
    }

    private fun findCellViewModelById(cellId: String): CellViewModel? {
        return state.value.viewModels
            .firstOrNull { vm -> vm.model.id == cellId }
    }

    private fun isDataChanged(): Boolean {
        return interactor.isDriverRunning() != isDriverRunning.value ||
            interactor.isGatewayRunning() != isGatewayRunning.value
    }

    private fun createTopBarState(): TopBarState =
        TopBarState(
            title = resourceProvider.getString(R.string.settings),
            isBackVisible = true
        )

    companion object {
        private const val ACTION_PROD_URL_SELECTED = 100
        private const val ACTION_DEBUG_URL_SELECTED = 101

        private const val ACTION_DELAY_SCALE_1X = 200
        private const val ACTION_DELAY_SCALE_2X = 201
        private const val ACTION_DELAY_SCALE_3X = 202

        private const val ACTION_NUMBER_OF_RETRIES_3 = 303
        private const val ACTION_NUMBER_OF_RETRIES_4 = 304
        private const val ACTION_NUMBER_OF_RETRIES_5 = 305
        private const val ACTION_NUMBER_OF_RETRIES_6 = 306
    }
}