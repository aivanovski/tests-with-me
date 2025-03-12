package com.github.aivanovski.testswithme.android.presentation.screens.flow

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.extensions.asFlow
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ButtonCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextWithIconCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextButtonCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogButton
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogState
import com.github.aivanovski.testswithme.android.presentation.core.dialogFactories.OptionDialogFactory
import com.github.aivanovski.testswithme.android.presentation.core.dialogFactories.OptionDialogFactory.createApplicationOptionsDialog
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.FlowCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.FlowCellFactory.CellId
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.model.HistoryItemCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.ExternalAppData
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowData
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowIntent
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenMode
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowState
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowUiEvent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentArgs
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentScreenMode
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestScreenArgs
import com.github.aivanovski.testswithme.android.utils.formatError
import com.github.aivanovski.testswithme.android.utils.infiniteRepeatFlow
import com.github.aivanovski.testswithme.android.utils.toTerminalState
import com.github.aivanovski.testswithme.android.utils.toUids
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.utils.StringUtils
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

// TODO: rename to test screen
class FlowViewModel(
    private val interactor: FlowInteractor,
    private val cellFactory: FlowCellFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: FlowScreenArgs
) : BaseViewModel() {

    val state = MutableStateFlow(FlowState(terminalState = TerminalState.Loading))

    private val _events = Channel<FlowUiEvent>(capacity = Channel.BUFFERED)
    val events: Flow<FlowUiEvent> = _events.receiveAsFlow()

    private val intents = Channel<FlowIntent>()
    private val startedJobUids = mutableListOf<String>()
    private var isSubscribed = false
    private var data: FlowData? = null
    private var appData: ExternalAppData? = null
    private var isDriverRunning = interactor.isDriverServiceRunning()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))

        if (!isSubscribed) {
            isSubscribed = true

            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(FlowIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent, state.value) }
                    .flowOn(Dispatchers.IO)
                    .collect { newState ->
                        state.value = newState
                    }
            }

            viewModelScope.launch {
                infiniteRepeatFlow(1000.milliseconds)
                    .collect {
                        if (isDriverStatusChanged() && state.value.isInDataState()) {
                            sendIntent(FlowIntent.ReBuildState)
                        }
                    }
            }
        } else {
            sendIntent(FlowIntent.ReBuildState)
        }
    }

    override fun handleCellIntent(intent: BaseCellIntent) {
        val cellId = when (intent) {
            // is ButtonCellIntent.OnClick -> onButtonCellClicked(intent.cellId)
            is HistoryItemCellIntent.OnItemClick -> intent.cellId
            is FlowCellIntent.OnClick -> intent.cellId
            is ButtonCellIntent.OnClick -> intent.cellId
            is TextButtonCellIntent.OnClick -> intent.cellId
            is HeaderCellIntent.OnIconClick -> intent.cellId
            is LabeledTextWithIconCellIntent.OnIconClick -> intent.cellId
            else -> return
        }

        when {
            CellId.hasFlowUid(cellId) -> {
                CellId.extractFlowUid(cellId)?.let { uid ->
                    onFlowClicked(uid)
                }
            }

            CellId.hasJobUid(cellId) -> {
                CellId.extractJobUid(cellId)?.let { uid ->
                    navigateToLocalJobReport(jobUid = uid)
                }
            }

            CellId.hasRunUid(cellId) -> {
                CellId.extractRunUid(cellId)?.let { uid ->
                    navigateToFlowRunReport(flowRunUid = uid)
                }
            }

            cellId == CellId.MORE_STEPS_BUTTON -> navigateToTestContentScreen()
            cellId == CellId.STEPS_HEADER -> navigateToTestContentScreen()
            cellId == CellId.APPLICATION_NAME -> showApplicationOptionsDialog()
            cellId == CellId.APPLICATION_BUTTON -> navigateToProjectDownloadsPage()
            cellId == CellId.RUN_TEST_BUTTON -> onRunButtonClicked()
            cellId == CellId.DRIVER_BUTTON -> onEnableDriverButtonClicked()
        }
    }

    fun sendIntent(intent: FlowIntent) {
        intents.trySend(intent)
    }

    private fun sendUiEvent(event: FlowUiEvent) {
        _events.trySend(event)
    }

    private fun handleIntent(
        intent: FlowIntent,
        state: FlowState
    ): Flow<FlowState> {
        return when (intent) {
            FlowIntent.Initialize -> loadData()
            FlowIntent.ReBuildState -> rebuildState()
            FlowIntent.OnDismissErrorDialog -> onDismissErrorDialog(state)
            FlowIntent.OnDismissFlowDialog -> onDismissFlowDialog(state)
            FlowIntent.OnUploadButtonClick -> onAddButtonClick()
            FlowIntent.OnDismissOptionDialog -> dismissOptionDialog()
            is FlowIntent.OnFlowDialogActionClick -> onFlowDialogActionClick(intent, state)
            is FlowIntent.OnFlowClick -> onFlowClicked(intent, state)
            is FlowIntent.RunFlow -> startFlow(intent, state)
            is FlowIntent.RunFlows -> startFlowGroup(intent, state)
            is FlowIntent.OnOptionDialogClick -> handleOptionDialogAction(intent.action)
        }
    }

    private fun onRunButtonClicked() {
        val data = data ?: return

        when (args.mode) {
            is FlowScreenMode.Flow ->
                sendIntent(FlowIntent.RunFlow(args.mode.flowUid))

            is FlowScreenMode.Group ->
                sendIntent(FlowIntent.RunFlows(data.visibleFlows.toUids()))

            is FlowScreenMode.RemainedFlows ->
                sendIntent(FlowIntent.RunFlows(data.visibleFlows.toUids()))

            is FlowScreenMode.LocalFlow ->
                sendIntent(FlowIntent.RunFlow(args.mode.flowUid))
        }
    }

    private fun onEnableDriverButtonClicked() {
        sendUiEvent(FlowUiEvent.ShowAccessibilityServices)
    }

    private fun showApplicationOptionsDialog() {
        state.value = state.value.copy(
            optionDialogState = createApplicationOptionsDialog(
                project = data?.project,
                resourceProvider = resourceProvider
            )
        )
    }

    private fun handleOptionDialogAction(action: DialogAction): Flow<FlowState> {
        when (action.actionId) {
            OptionDialogFactory.ACTION_OPEN_DOWNLOADS_PAGE -> {
                navigateToProjectDownloadsPage()
            }

            OptionDialogFactory.ACTION_OPEN_WEBSITE -> {
                navigateToProjectWebsitePage()
            }
        }

        return dismissOptionDialog()
    }

    private fun navigateToProjectDownloadsPage() {
        val downloadsUrl = data?.project?.downloadUrl ?: return

        sendUiEvent(FlowUiEvent.OpenUrl(downloadsUrl))
    }

    private fun navigateToProjectWebsitePage() {
        val websiteUrl = data?.project?.siteUrl ?: return

        sendUiEvent(FlowUiEvent.OpenUrl(websiteUrl))
    }

    private fun dismissOptionDialog(): Flow<FlowState> =
        state.value.copy(
            optionDialogState = null
        ).asFlow()

    private fun onFlowClicked(cellId: String) {
        sendIntent(FlowIntent.OnFlowClick(flowUid = cellId))
    }

    private fun startFlowGroup(
        intent: FlowIntent.RunFlows,
        state: FlowState
    ): Flow<FlowState> {
        val isDriverRunning = interactor.isDriverServiceRunning()
        if (!isDriverRunning) {
            return flowOf(
                state.copy(
                    flowDialogState = createDriverNotRunningDialogState()
                )
            )
        }

        val flowUids = intent.flowUids
        val jobUids = flowUids.indices.map { newJobUid() }
        startedJobUids.addAll(jobUids)

        return flow {
            emit(
                state.copy(
                    flowDialogState = createPrepareDialogState()
                )
            )

            val startResult = interactor.startFlows(flowUids, jobUids)
            if (startResult.isLeft()) {
                emit(
                    state.copy(
                        errorDialogMessage = startResult.formatError(resourceProvider)
                    )
                )
                return@flow
            }

            delay(2000L) // TODO: refactor

            emit(
                state.copy(
                    flowDialogState = createWaitingForLaunchDialogState()
                )
            )
        }
    }

    private fun startFlow(
        intent: FlowIntent.RunFlow,
        state: FlowState
    ): Flow<FlowState> {
        val isDriverRunning = interactor.isDriverServiceRunning()
        if (!isDriverRunning) {
            return flowOf(
                state.copy(
                    flowDialogState = createDriverNotRunningDialogState()
                )
            )
        }

        val jobUid = newJobUid()
        startedJobUids.add(jobUid)

        return flow {
            emit(
                state.copy(
                    flowDialogState = createPrepareDialogState()
                )
            )

            val startResult = interactor.startFlow(intent.flowUid, jobUid)
            if (startResult.isLeft()) {
                emit(
                    state.copy(
                        errorDialogMessage = startResult.formatError(resourceProvider)
                    )
                )
                return@flow
            }

            delay(2000L) // TODO: refactor

            emit(
                state.copy(
                    flowDialogState = createWaitingForLaunchDialogState()
                )
            )
        }
    }

    private fun loadData(): Flow<FlowState> {
        return interactor.loadData(args.mode)
            .map { loadDataResult ->
                if (loadDataResult.isLeft()) {
                    val terminalState = loadDataResult.toTerminalState(resourceProvider)
                    return@map FlowState(terminalState = terminalState)
                }

                data = loadDataResult.unwrap()
                val data = loadDataResult.unwrap()

                appData = interactor.getApplicationData(data.project?.packageName ?: "")
                    .getOrNull()
                isDriverRunning = interactor.isDriverServiceRunning()

                rootViewModel.sendIntent(SetTopBarState(createTopBarState()))

                buildScreenState(data)
            }
            .onStart {
                emit(FlowState(terminalState = TerminalState.Loading))
            }
    }

    private fun rebuildState(): Flow<FlowState> {
        val data = this.data ?: return emptyFlow()

        appData = if (data.project != null) {
            interactor.getApplicationData(data.project.packageName).getOrNull()
        } else {
            null
        }
        isDriverRunning = interactor.isDriverServiceRunning()

        return flowOf(buildScreenState(data))
    }

    private fun buildScreenState(data: FlowData): FlowState {
        return when (args.mode) {
            is FlowScreenMode.LocalFlow -> {
                FlowState(
                    viewModels = cellFactory.createLocalFlowCellViewModels(
                        data = data,
                        isDriverRunning = isDriverRunning,
                        intentProvider = intentProvider
                    ),
                    isUploadButtonVisible = true
                )
            }

            is FlowScreenMode.Flow -> {
                FlowState(
                    viewModels = cellFactory.createFlowCellViewModels(
                        data = data,
                        requiredAppVersion = args.mode.requiredVersion,
                        installedAppData = appData,
                        isDriverRunning = isDriverRunning,
                        intentProvider = intentProvider
                    )
                )
            }

            is FlowScreenMode.Group -> {
                FlowState(
                    viewModels = cellFactory.createGroupCellViewModels(
                        data = data,
                        installedAppData = appData,
                        isDriverRunning = isDriverRunning,
                        intentProvider = intentProvider
                    )
                )
            }

            is FlowScreenMode.RemainedFlows -> {
                FlowState(
                    viewModels = cellFactory.createRemainedFlowsCellViewModels(
                        data = data,
                        requiredAppVersion = args.mode.version,
                        installedAppData = appData,
                        isDriverRunning = isDriverRunning,
                        intentProvider = intentProvider
                    )
                )
            }
        }
    }

    private fun onDismissErrorDialog(state: FlowState): Flow<FlowState> {
        return flowOf(
            state.copy(errorDialogMessage = null)
        )
    }

    private fun onDismissFlowDialog(state: FlowState): Flow<FlowState> {
        return flowOf(
            state.copy(flowDialogState = null)
        )
    }

    private fun onAddButtonClick(): Flow<FlowState> {
        val flow = getCurrentFlow() ?: return emptyFlow()

        router.navigateTo(
            Screen.UploadTest(
                UploadTestScreenArgs(
                    flowUid = flow.uid
                )
            )
        )

        return emptyFlow()
    }

    private fun onFlowClicked(
        intent: FlowIntent.OnFlowClick,
        state: FlowState
    ): Flow<FlowState> {
        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.Flow(intent.flowUid),
                    screenTitle = StringUtils.EMPTY
                )
            )
        )

        return emptyFlow()
    }

    private fun onFlowDialogActionClick(
        intent: FlowIntent.OnFlowDialogActionClick,
        state: FlowState
    ): Flow<FlowState> {
        return when (intent.actionId) {
            DIALOG_ACTION_LAUNCH_SERVICES -> {
                sendUiEvent(FlowUiEvent.ShowAccessibilityServices)
                emptyFlow()
            }

            DIALOG_ACTION_CANCEL_FLOW -> {
                cancelStartedJobs()
            }

            else -> emptyFlow()
        }
    }

    private fun cancelStartedJobs(): Flow<FlowState> {
        return flow {
            val jobUids = startedJobUids.toList()

            Timber.d("Cancelling job: %s", jobUids)

            for (jobUid in jobUids) {
                val cancelResult = interactor.cancelJob(jobUid)
                if (cancelResult.isLeft()) {
                    val terminalState = cancelResult.toTerminalState(resourceProvider)

                    emit(state.value.copy(terminalState = terminalState))
                    return@flow
                }

                Timber.d("Job was cancelled: %s", jobUid)
            }

            emit(
                state.value.copy(
                    flowDialogState = null
                )
            )
        }
    }

    private fun createTopBarState(): TopBarState {
        val data = this.data
            ?: return TopBarState(
                title = args.screenTitle,
                isBackVisible = true
            )

        val title = when (args.mode) {
            is FlowScreenMode.LocalFlow -> data.visibleFlows.first().name
            is FlowScreenMode.Flow -> data.visibleFlows.first().name
            is FlowScreenMode.Group -> data.group?.name ?: StringUtils.EMPTY
            is FlowScreenMode.RemainedFlows -> resourceProvider.getString(R.string.remained_tests)
        }

        return TopBarState(
            title = title,
            isBackVisible = true
        )
    }

    private fun createPrepareDialogState(): MessageDialogState {
        return MessageDialogState(
            title = null,
            message = resourceProvider.getString(R.string.prepare_flow_message),
            isCancellable = false,
            actionButton = MessageDialogButton.ActionButton(
                title = resourceProvider.getString(R.string.cancel),
                actionId = DIALOG_ACTION_CANCEL_FLOW
            )
        )
    }

    private fun createDriverNotRunningDialogState(): MessageDialogState {
        return MessageDialogState(
            title = resourceProvider.getString(R.string.driver_is_not_enabled_title),
            message = resourceProvider.getString(R.string.driver_is_not_enabled_message),
            isCancellable = true,
            actionButton = MessageDialogButton.ActionButton(
                title = resourceProvider.getString(R.string.services),
                actionId = DIALOG_ACTION_LAUNCH_SERVICES
            )
        )
    }

    private fun createWaitingForLaunchDialogState(): MessageDialogState {
        return MessageDialogState(
            title = null,
            message = resourceProvider.getString(R.string.awaiting_start_message),
            isCancellable = false,
            actionButton = MessageDialogButton.ActionButton(
                title = resourceProvider.getString(R.string.cancel),
                actionId = DIALOG_ACTION_CANCEL_FLOW
            )
        )
    }

    private fun navigateToFlowRunReport(flowRunUid: String) {
        val flow = getCurrentFlow() ?: return

        router.navigateTo(
            Screen.TestContent(
                TestContentArgs(
                    screenTitle = flow.name,
                    flowUid = flow.uid,
                    mode = TestContentScreenMode.RemoteRun(flowRunUid = flowRunUid)
                )
            )
        )
    }

    private fun navigateToLocalJobReport(jobUid: String) {
        val flow = getCurrentFlow() ?: return

        router.navigateTo(
            Screen.TestContent(
                TestContentArgs(
                    screenTitle = flow.name,
                    flowUid = flow.uid,
                    mode = TestContentScreenMode.LocalRun(jobUid = jobUid)
                )
            )
        )
    }

    private fun navigateToTestContentScreen() {
        val flow = getCurrentFlow() ?: return

        router.navigateTo(
            Screen.TestContent(
                TestContentArgs(
                    screenTitle = flow.name,
                    flowUid = flow.uid,
                    mode = TestContentScreenMode.FlowContent
                )
            )
        )
    }

    private fun newJobUid(): String = UUID.randomUUID().toString()

    private fun isDriverStatusChanged(): Boolean {
        return interactor.isDriverServiceRunning() != isDriverRunning
    }

    private fun getCurrentFlow(): FlowEntry? {
        val data = this.data ?: return null

        return when (args.mode) {
            is FlowScreenMode.LocalFlow -> data.visibleFlows.firstOrNull()
            is FlowScreenMode.Flow -> data.visibleFlows.firstOrNull()
            else -> null
        }
    }

    private fun getProjectOrNull(): ProjectEntry? {
        return data?.project
    }

    private fun FlowState.isInDataState(): Boolean {
        return viewModels.isNotEmpty() && terminalState == null
    }

    companion object {
        private const val DIALOG_ACTION_CANCEL_FLOW = 1
        private const val DIALOG_ACTION_LAUNCH_SERVICES = 2
        // private const val DIALOG_ACTION_OPEN_WEBSITE = 3
        // private const val DIALOG_ACTION_OPEN_DOWNLOADS = 4
    }
}