package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTextCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextChipRowCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.ScreenState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.toTerminalState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenMode
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
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.utils.formatErrorMessage
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProjectDashboardViewModel(
    private val interactor: ProjectDashboardInteractor,
    private val cellFactory: ProjectDashboardCellFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: ProjectDashboardScreenArgs
) : BaseViewModel() {

    val state = MutableStateFlow(ProjectDashboardState())
    private val intents = Channel<ProjectDashboardIntent>()
    private var isSubscribed = false
    private var data: ProjectDashboardData? = null
    private var selectedVersionName: String? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))

        if (!isSubscribed) {
            isSubscribed = true

            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(ProjectDashboardIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent) }
                    .collect { newState ->
                        state.value = newState
                    }
            }
        }
    }

    override fun handleCellIntent(intent: BaseCellIntent) {
        when (intent) {
            is HeaderCellIntent.OnIconClick -> onHeaderCellClicked(intent.cellId)
            is TextChipRowCellIntent.OnClick -> onVersionsCellClicked(intent.chipIndex)
            is IconTextCellIntent.OnClick -> navigateToRemainedFlowScreen(intent.cellId)
            is FlowCellIntent.OnClick -> navigateToFlowScreen(intent.cellId)
            is GroupCellIntent.OnClick -> navigateToGroupScreen(intent.cellId)
            is GroupCellIntent.OnDetailsClick -> navigateToGroupDetailsScreen(intent.cellId)
        }
    }

    fun sendIntent(intent: ProjectDashboardIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(intent: ProjectDashboardIntent): Flow<ProjectDashboardState> {
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
        }
    }

    private fun loadData(versionName: String? = null): Flow<ProjectDashboardState> {
        return flow {
            emit(ProjectDashboardState(screenState = ScreenState.Loading))

            val loadDataResult = interactor.loadData(
                projectUid = args.projectUid,
                versionName = versionName
            )
            if (loadDataResult.isLeft()) {
                val terminalState = loadDataResult.unwrapError()
                    .formatErrorMessage(resourceProvider)
                    .toTerminalState()

                emit(ProjectDashboardState(screenState = terminalState))
                return@flow
            }

            data = loadDataResult.unwrap()
            val data = loadDataResult.unwrap()

            selectedVersionName = versionName ?: data.versions.firstOrNull()?.name

            if (data.allGroups.isNotEmpty() || data.allFlows.isNotEmpty()) {
                val viewModels = cellFactory.createCellViewModels(
                    data = data,
                    selectedVersion = selectedVersionName,
                    intentProvider = intentProvider
                )
                emit(ProjectDashboardState(viewModels = viewModels))
            } else {
                val empty = ScreenState.Empty(
                    message = resourceProvider.getString(R.string.no_tests_in_project_message)
                )
                emit(ProjectDashboardState(screenState = empty))
            }
        }
    }

    private fun onHeaderCellClicked(cellId: String) {
        when (cellId) {
            CellId.REMAINED_FLOWS_HEADER -> navigateToRemainedFlowsScreen()
            CellId.GROUPS_HEADER -> navigateToGroupsScreen()
            else -> throw IllegalArgumentException("Invalid cellId: $cellId")
        }
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
        val version = getSelectedVersion()

        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.RemainedFlows(
                        projectUid = args.projectUid,
                        version = version
                    ),
                    screenTitle = resourceProvider.getString(R.string.remained_tests)
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
}