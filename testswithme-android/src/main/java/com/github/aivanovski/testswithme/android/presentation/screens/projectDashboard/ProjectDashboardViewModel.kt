package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard

import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.presentation.core.MviViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ButtonCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTextCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTableCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextWithIconCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextChipRowCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TitleWithIconCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogButton
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogState
import com.github.aivanovski.testswithme.android.presentation.core.dialogFactories.OptionDialogFactory
import com.github.aivanovski.testswithme.android.presentation.core.dialogFactories.OptionDialogFactory.createProgressOptionDialog
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenMode
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowSelection
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model.GroupEditorScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.GroupCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.ProjectDashboardCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.ProjectDashboardCellFactory.CellId
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardData
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardIntent
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardState
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardUiEvent
import com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model.ResetRunsScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.utils.toTerminalState
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.utils.mutableStateFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.core.time.measureDuration

class ProjectDashboardViewModel(
    private val interactor: ProjectDashboardInteractor,
    private val cellFactory: ProjectDashboardCellFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: ProjectDashboardScreenArgs
) : MviViewModel<ProjectDashboardState, ProjectDashboardIntent>(
    initialState = ProjectDashboardState(terminalState = TerminalState.Loading),
    initialIntent = ProjectDashboardIntent.Initialize
) {

    private val _events = Channel<ProjectDashboardUiEvent>(capacity = Channel.BUFFERED)
    val events: Flow<ProjectDashboardUiEvent> = _events.receiveAsFlow()

    private var data by mutableStateFlow<ProjectDashboardData?>(null)
    private var selectedVersionName by mutableStateFlow<String?>(null)

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))

        sendIntent(ProjectDashboardIntent.ReloadData)
    }

    override fun handleCellIntent(intent: BaseCellIntent) {
        when (intent) {
            is HeaderCellIntent.OnIconClick -> onHeaderCellClicked(intent.cellId)
            is TextChipRowCellIntent.OnClick -> onVersionsCellClicked(intent.chipIndex)
            is IconTextCellIntent.OnClick -> navigateToRemainedFlowScreen(intent.cellId)
            is FlowCellIntent.OnClick -> navigateToFlowScreen(intent.cellId)
            is GroupCellIntent.OnClick -> navigateToGroupScreen(intent.cellId)
            is GroupCellIntent.OnDetailsClick -> navigateToGroupDetailsScreen(intent.cellId)
            is TitleWithIconCellIntent.OnIconClick -> onTitleCellClicked(intent.cellId)
            is LabeledTextWithIconCellIntent.OnIconClick -> showApplicationOptionsDialog()
            is ButtonCellIntent.OnClick -> navigateToProjectDownloadsPage()
            is LabeledTableCellIntent.OnColumnClick -> onTableColumnClicked(intent.columnIndex)
        }
    }

    override fun handleIntent(intent: ProjectDashboardIntent): Flow<ProjectDashboardState> {
        return when (intent) {
            ProjectDashboardIntent.Initialize -> loadData()

            ProjectDashboardIntent.ReloadData -> loadData(
                versionName = getSelectedVersion()?.name
            )

            is ProjectDashboardIntent.OnVersionClick -> loadData(
                versionName = intent.versionName
            )

            ProjectDashboardIntent.OnAddButtonClick -> {
                navigateToNewGroupScreen()
                emptyFlow()
            }

            ProjectDashboardIntent.OnDismissOptionDialog -> dismissOptionDialog()
            is ProjectDashboardIntent.OnDismissMessageDialog -> dismissMessageDialog()
            is ProjectDashboardIntent.OnOptionDialogClick -> handleDialogAction(intent.action)
            is ProjectDashboardIntent.OnMessageDialogClick -> handleDialogAction(intent.action)
        }
    }

    private fun sendUiEvent(event: ProjectDashboardUiEvent) {
        _events.trySend(event)
    }

    private fun loadData(versionName: String? = null): Flow<ProjectDashboardState> {
        return interactor.loadData(
            projectUid = args.projectUid,
            versionName = versionName
        )
            .map { loadDataResult ->
                if (loadDataResult.isLeft()) {
                    val terminalState = loadDataResult.toTerminalState(resourceProvider)
                    return@map ProjectDashboardState(terminalState = terminalState)
                }

                data = loadDataResult.unwrap()
                val data = loadDataResult.unwrap()

                selectedVersionName = versionName ?: data.versions.firstOrNull()?.name

                val isNotEmpty = (data.allFlows.isNotEmpty() || data.allGroups.size > 1)
                if (isNotEmpty) {
                    val viewModels = cellFactory.createCellViewModels(
                        data = data,
                        selectedVersion = selectedVersionName,
                        intentProvider = intentProvider
                    )
                    ProjectDashboardState(viewModels = viewModels)
                } else {
                    val empty = TerminalState.Empty(
                        message = resourceProvider.getString(R.string.empty_project_message)
                    )
                    ProjectDashboardState(terminalState = empty)
                }
            }
            .onStart {
                emit(ProjectDashboardState(terminalState = TerminalState.Loading))
            }
    }

    private fun onTableColumnClicked(columnIndex: Int) {
        when (columnIndex) {
            0 -> navigateToPassedFlowsScreen()
            1 -> navigateToFailedFlowsScreen()
            2 -> navigateToRemainedFlowsScreen()
        }
    }

    private fun handleDialogAction(action: DialogAction): Flow<ProjectDashboardState> {
        return when (action.actionId) {
            OptionDialogFactory.ACTION_RESET_PROGRESS -> {
                navigateToResetRunsScreen()
                dismissOptionDialog()
            }

            OptionDialogFactory.ACTION_OPEN_DOWNLOADS_PAGE -> {
                navigateToProjectDownloadsPage()
                dismissOptionDialog()
            }

            OptionDialogFactory.ACTION_REQUEST_SYNC -> {
                onRequestSyncClicked()
            }

            OptionDialogFactory.ACTION_OPEN_WEBSITE -> {
                navigateToProjectWebsitePage()
                dismissOptionDialog()
            }

            ACTION_OK -> {
                dismissMessageDialog()
            }

            ACTION_CANCEL -> {
                dismissMessageDialog()
            }

            else -> dismissOptionDialog()
        }
    }

    private fun onRequestSyncClicked(): Flow<ProjectDashboardState> {
        val projectUid = data?.project?.uid ?: return emptyFlow()

        return flow {
            emit(state.value.copy(optionDialogState = null))

            val requestSyncResult = interactor.requestProjectSync(projectUid)
            if (requestSyncResult.isLeft()) {
                val terminalState = requestSyncResult.toTerminalState(resourceProvider)
                emit(state.value.copy(terminalState = terminalState))
                return@flow
            }

            emit(
                state.value.copy(
                    messageDialogState = MessageDialogState(
                        title = null,
                        message = resourceProvider.getString(R.string.sync_request_success_message),
                        isCancellable = true,
                        actionButton = MessageDialogButton.ActionButton(
                            title = resourceProvider.getString(R.string.ok),
                            actionId = ACTION_OK
                        )
                    )
                )
            )
        }
    }

    private fun onHeaderCellClicked(cellId: String) {
        when (cellId) {
            CellId.REMAINED_FLOWS_HEADER -> navigateToRemainedFlowsScreen()
            CellId.GROUPS_HEADER -> navigateToGroupsScreen()
            else -> throw IllegalArgumentException("Invalid cellId: $cellId")
        }
    }

    private fun onTitleCellClicked(cellId: String) {
        when (cellId) {
            CellId.TITLE -> showProgressOptionsDialog()
            else -> throw IllegalArgumentException("Invalid cellId: $cellId")
        }
    }

    private fun showApplicationOptionsDialog() {
        state.value = state.value.copy(
            optionDialogState = OptionDialogFactory.createApplicationOptionsDialog(
                project = data?.project,
                resourceProvider = resourceProvider
            )
        )
    }

    private fun onVersionsCellClicked(versionIndex: Int) {
        val data = this.data ?: return
        val newVersion = data.versions.getOrNull(versionIndex) ?: return

        if (newVersion.name != selectedVersionName) {
            sendIntent(ProjectDashboardIntent.OnVersionClick(newVersion.name))
        }
    }

    private fun navigateToGroupsScreen() {
        router.navigateTo(
            Screen.Groups(
                GroupsScreenArgs(
                    projectUid = args.projectUid,
                    groupUid = data?.rootGroup?.uid
                )
            )
        )
    }

    private fun navigateToRemainedFlowsScreen() {
        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.FlowList(
                        projectUid = args.projectUid,
                        version = getSelectedVersion(),
                        selection = FlowSelection.Remained
                    ),
                    screenTitle = resourceProvider.getString(R.string.remained_tests)
                )
            )
        )
    }

    private fun navigateToPassedFlowsScreen() {
        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.FlowList(
                        projectUid = args.projectUid,
                        version = getSelectedVersion(),
                        selection = FlowSelection.Passed
                    ),
                    screenTitle = resourceProvider.getString(R.string.passed_tests)
                )
            )
        )
    }

    private fun navigateToFailedFlowsScreen() {
        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.FlowList(
                        projectUid = args.projectUid,
                        version = getSelectedVersion(),
                        selection = FlowSelection.Failed
                    ),
                    screenTitle = resourceProvider.getString(R.string.failed_tests)
                )
            )
        )
    }

    private fun navigateToRemainedFlowScreen(flowUid: String) {
        val flow = findFlowByUid(flowUid) ?: return
        val version = getSelectedVersion()

        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.Flow(
                        flowUid = flowUid,
                        requiredVersion = version
                    ),
                    screenTitle = flow.name
                )
            )
        )
    }

    private fun navigateToFlowScreen(flowUid: String) {
        val flow = findFlowByUid(flowUid) ?: return

        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.Flow(flowUid),
                    screenTitle = flow.name
                )
            )
        )
    }

    private fun navigateToGroupDetailsScreen(groupUid: String) {
        val group = findGroupByUid(groupUid) ?: return

        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.Group(groupUid),
                    screenTitle = group.name
                )
            )
        )
    }

    private fun navigateToGroupScreen(groupUid: String) {
        router.navigateTo(
            Screen.Groups(
                GroupsScreenArgs(
                    projectUid = args.projectUid,
                    groupUid = groupUid
                )
            )
        )
    }

    private fun navigateToNewGroupScreen() {
        router.navigateTo(
            Screen.GroupEditor(
                GroupEditorScreenArgs.NewGroup(
                    projectUid = args.projectUid,
                    parentGroupUid = null
                )
            )
        )
        router.setResultListener(Screen.GroupEditor::class) { _ ->
            sendIntent(ProjectDashboardIntent.ReloadData)
        }
    }

    private fun navigateToResetRunsScreen() {
        router.navigateTo(
            Screen.ResetRuns(
                ResetRunsScreenArgs(
                    projectUid = args.projectUid
                )
            )
        )
    }

    private fun navigateToProjectDownloadsPage() {
        val downloadsUrl = data?.project?.downloadUrl ?: return

        sendUiEvent(ProjectDashboardUiEvent.OpenUrl(downloadsUrl))
    }

    private fun navigateToProjectWebsitePage() {
        val websiteUrl = data?.project?.siteUrl ?: return

        sendUiEvent(ProjectDashboardUiEvent.OpenUrl(websiteUrl))
    }

    private fun showProgressOptionsDialog() {
        state.value = state.value.copy(
            optionDialogState = createProgressOptionDialog(resourceProvider)
        )
    }

    private fun dismissOptionDialog(): Flow<ProjectDashboardState> =
        flowOf(state.value.copy(optionDialogState = null))

    private fun dismissMessageDialog(): Flow<ProjectDashboardState> =
        flowOf(state.value.copy(messageDialogState = null))

    private fun findGroupByUid(groupUid: String): GroupEntry? {
        return data?.allGroups?.firstOrNull { group -> group.uid == groupUid }
    }

    private fun findFlowByUid(flowUid: String): FlowEntry? {
        return data?.allFlows?.firstOrNull { flow -> flow.uid == flowUid }
    }

    private fun getSelectedVersion(): AppVersion? {
        return data?.versions?.firstOrNull { version -> version.name == selectedVersionName }
    }

    private fun createTopBarState(): TopBarState {
        return TopBarState(
            title = args.screenTitle,
            isBackVisible = true
        )
    }

    companion object {

        private const val ACTION_OK = 101
        private const val ACTION_CANCEL = 102
    }
}