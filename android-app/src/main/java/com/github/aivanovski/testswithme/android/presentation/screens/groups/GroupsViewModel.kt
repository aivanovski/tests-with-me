package com.github.aivanovski.testswithme.android.presentation.screens.groups

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.toScreenState
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.OptionDialogState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenMode
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model.GroupEditorScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.GroupsCellModelFactory
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.GroupCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsData
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsState
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.utils.formatError
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
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

class GroupsViewModel(
    private val interactor: GroupsInteractor,
    private val cellFactory: GroupsCellModelFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: GroupsScreenArgs
) : BaseViewModel() {

    val state = MutableStateFlow(GroupsState(terminalState = TerminalState.Loading))
    private val intents = Channel<GroupsIntent>()

    private var data = MutableStateFlow<GroupsData?>(null)
    private var selectedGroup = MutableStateFlow<GroupEntry?>(null)
    private var selectedFlow = MutableStateFlow<FlowEntry?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createInitialTopBarState()))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))

        doOnceWhenStarted {
            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(GroupsIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent) }
                    .flowOn(Dispatchers.IO)
                    .collect { newState ->
                        state.value = newState
                    }
            }
        }
    }

    override fun handleCellIntent(intent: BaseCellIntent) {
        when (intent) {
            is GroupCellIntent.OnClick -> navigateToGroupsScreen(intent.cellId)
            is GroupCellIntent.OnLongClick -> showGroupOptionDialog(intent.cellId)
            is GroupCellIntent.OnDetailsClick -> navigateToFlowGroupScreen(intent.cellId)
            is FlowCellIntent.OnClick -> navigateToFlowScreen(intent.cellId)
            is FlowCellIntent.OnLongClick -> showFlowOptionDialog(intent.cellId)
        }
    }

    fun sendIntent(intent: GroupsIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(intent: GroupsIntent): Flow<GroupsState> {
        return when (intent) {
            GroupsIntent.Initialize -> loadData()
            GroupsIntent.ReloadData -> loadData()
            GroupsIntent.OnDismissOptionDialog -> dismissOptionDialog()
            is GroupsIntent.OnOptionDialogClick -> handleDialogAction(intent.action)

            GroupsIntent.OnAddButtonClick -> {
                navigateToNewGroupScreen()
                emptyFlow()
            }
        }
    }

    private fun handleDialogAction(action: DialogAction): Flow<GroupsState> {
        return when (action.actionId) {
            ACTION_EDIT_GROUP -> onEditGroupClicked()
            ACTION_REMOVE_GROUP -> onRemoveGroupClicked()
            ACTION_REMOVE_FLOW -> onRemoveFlowClicked()
            else -> dismissOptionDialog()
        }
    }

    private fun navigateToFlowScreen(flowUid: String) {
        val flow = data.value?.flows
            ?.firstOrNull { flow -> flow.uid == flowUid }
            ?: return

        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.Flow(flowUid),
                    screenTitle = flow.name
                )
            )
        )
    }

    private fun navigateToGroupsScreen(groupUid: String) {
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
                    parentGroupUid = args.groupUid
                )
            )
        )
        router.setResultListener(Screen.GroupEditor::class) { _ ->
            sendIntent(GroupsIntent.ReloadData)
        }
    }

    private fun navigateToEditGroupScreen(group: GroupEntry) {
        router.navigateTo(
            Screen.GroupEditor(
                GroupEditorScreenArgs.EditGroup(
                    groupUid = group.uid,
                    screenTitle = group.name
                )
            )
        )
        router.setResultListener(Screen.GroupEditor::class) { _ ->
            sendIntent(GroupsIntent.ReloadData)
        }
    }

    private fun navigateToFlowGroupScreen(groupUid: String) {
        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.Group(groupUid),
                    screenTitle = StringUtils.EMPTY
                )
            )
        )
    }

    private fun loadData(): Flow<GroupsState> {
        return interactor.loadDataFlow(
            projectUid = args.projectUid,
            groupUid = args.groupUid
        )
            .map { loadDataResult ->
                if (loadDataResult.isLeft()) {
                    val terminalState = loadDataResult
                        .formatError(resourceProvider)
                        .toScreenState()
                    return@map GroupsState(terminalState = terminalState)
                }

                Timber.d("thread=${Thread.currentThread()}")

                data.value = loadDataResult.unwrap()
                val data = loadDataResult.unwrap()

                val topBarState = TopBarState(
                    title = data.group?.name ?: resourceProvider.getString(R.string.groups),
                    isBackVisible = true
                )
                rootViewModel.sendIntent(SetTopBarState(topBarState))

                if (data.flows.isNotEmpty() || data.groups.isNotEmpty()) {
                    val viewModels = cellFactory.createCellViewModels(data, intentProvider)
                    GroupsState(viewModels = viewModels)
                } else {
                    val emptyState = TerminalState.Empty(
                        message = resourceProvider.getString(R.string.this_group_is_empty)
                    )
                    GroupsState(terminalState = emptyState)
                }
            }
            .onStart {
                emit(GroupsState(terminalState = TerminalState.Loading))
            }
            .flowOn(Dispatchers.IO)
    }

    private fun createInitialTopBarState(): TopBarState {
        val title = data.value?.group?.name ?: resourceProvider.getString(R.string.groups)

        return TopBarState(
            title = title,
            isBackVisible = true
        )
    }

    private fun showGroupOptionDialog(groupUid: String) {
        val group = data.value?.groups
            ?.firstOrNull { group -> group.uid == groupUid }
            ?: return

        selectedGroup.value = group

        val options = listOf(
            resourceProvider.getString(R.string.edit),
            resourceProvider.getString(R.string.remove)
        )
        val actions = listOf(
            DialogAction(ACTION_EDIT_GROUP),
            DialogAction(ACTION_REMOVE_GROUP)
        )

        state.value = state.value.copy(
            optionDialogState = OptionDialogState(options, actions)
        )
    }

    private fun showFlowOptionDialog(flowUid: String) {
        val flow = data.value?.allFlows
            ?.firstOrNull { flow -> flow.uid == flowUid }
            ?: return

        selectedFlow.value = flow

        val options = listOf(
            resourceProvider.getString(R.string.remove)
        )
        val actions = listOf(
            DialogAction(ACTION_REMOVE_FLOW)
        )

        state.value = state.value.copy(
            optionDialogState = OptionDialogState(options, actions)
        )
    }

    private fun onEditGroupClicked(): Flow<GroupsState> {
        val group = selectedGroup.value ?: return emptyFlow()
        navigateToEditGroupScreen(group)
        return dismissOptionDialog()
    }

    private fun onRemoveGroupClicked(): Flow<GroupsState> {
        val group = selectedGroup.value ?: return emptyFlow()

        return flow {
            emit(
                state.value.copy(
                    terminalState = TerminalState.Loading,
                    optionDialogState = null
                )
            )

            val removeResult = interactor.removeGroup(group.uid)
            if (removeResult.isLeft()) {
                val terminalState = removeResult
                    .formatError(resourceProvider)
                    .toScreenState()

                emit(state.value.copy(terminalState = terminalState))
                return@flow
            }

            emitAll(loadData())
        }
    }

    private fun onRemoveFlowClicked(): Flow<GroupsState> {
        val flow = selectedFlow.value ?: return emptyFlow()

        return flow {
            emit(
                state.value.copy(
                    terminalState = TerminalState.Loading,
                    optionDialogState = null
                )
            )

            val removeResult = interactor.removeFlow(flow.uid)
            if (removeResult.isLeft()) {
                val terminalState = removeResult
                    .formatError(resourceProvider)
                    .toScreenState()

                emit(state.value.copy(terminalState = terminalState))
                return@flow
            }

            emitAll(loadData())
        }
    }

    private fun dismissOptionDialog(): Flow<GroupsState> {
        return flowOf(
            state.value.copy(
                optionDialogState = null
            )
        )
    }

    companion object {
        private const val ACTION_EDIT_GROUP = 100
        private const val ACTION_REMOVE_GROUP = 101
        private const val ACTION_REMOVE_FLOW = 102
    }
}