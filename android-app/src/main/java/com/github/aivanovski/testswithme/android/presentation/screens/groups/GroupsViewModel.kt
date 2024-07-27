package com.github.aivanovski.testswithme.android.presentation.screens.groups

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.Group
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenMode
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.GroupsCellModelFactory
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.GroupsCellViewModelFactory
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.GroupCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsState
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.utils.formatError
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.utils.StringUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val interactor: GroupsInteractor,
    private val modelFactory: GroupsCellModelFactory,
    private val viewModelFactory: GroupsCellViewModelFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: GroupsScreenArgs
) : BaseViewModel() {

    val state = MutableStateFlow<GroupsState>(GroupsState.NotInitialized)

    private val intents = Channel<GroupsIntent>()
    private var currentFlows: List<FlowEntry> = emptyList()
    private var currentGroups: List<Group> = emptyList()

    override fun start() {
        super.start()

        if (args.groupUid == null) {
            rootViewModel.sendIntent(RootIntent.SetTopBarState(createInitialTopBarState()))
        }

        if (state.value == GroupsState.NotInitialized) {
            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(GroupsIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent, state.value) }
                    .collect { newState ->
                        state.value = newState
                    }
            }
        }
    }

    override fun handleCellIntent(intent: BaseCellIntent) {
        when (intent) {
            is GroupCellIntent.OnClick -> sendIntent(GroupsIntent.OnGroupClicked(intent.cellId))

            is GroupCellIntent.OnDetailsClick ->
                sendIntent(GroupsIntent.OnGroupDetailsClicked(intent.cellId))

            is FlowCellIntent.OnClick -> sendIntent(GroupsIntent.OnFlowClicked(intent.cellId))
        }
    }

    fun sendIntent(intent: GroupsIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(
        intent: GroupsIntent,
        state: GroupsState
    ): Flow<GroupsState> {
        return when (intent) {
            GroupsIntent.Initialize -> loadData()
            is GroupsIntent.OnFlowClicked -> onFlowClicked(intent)
            is GroupsIntent.OnGroupClicked -> onGroupClicked(intent)
            is GroupsIntent.OnGroupDetailsClicked -> onGroupDetailsClicked(intent)
        }
    }

    private fun onFlowClicked(intent: GroupsIntent.OnFlowClicked): Flow<GroupsState> {
        val flow = currentFlows.firstOrNull { flow -> flow.uid == intent.flowUid }
            ?: return emptyFlow()

        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.Flow(intent.flowUid),
                    screenTitle = flow.name
                )
            )
        )

        return emptyFlow()
    }

    private fun onGroupClicked(intent: GroupsIntent.OnGroupClicked): Flow<GroupsState> {
        router.navigateTo(
            Screen.Groups(
                GroupsScreenArgs(
                    projectUid = args.projectUid,
                    groupUid = intent.groupUid
                )
            )
        )

        return emptyFlow()
    }

    private fun onGroupDetailsClicked(
        intent: GroupsIntent.OnGroupDetailsClicked
    ): Flow<GroupsState> {
        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = FlowScreenMode.Group(intent.groupUid),
                    screenTitle = StringUtils.EMPTY
                )
            )
        )

        return emptyFlow()
    }

    private fun loadData(): Flow<GroupsState> {
        return flow {
            emit(GroupsState.Loading)

            val getFlowsResult = interactor.getData(
                projectUid = args.projectUid,
                groupUid = args.groupUid
            )
            if (getFlowsResult.isRight()) {
                val data = getFlowsResult.unwrap()

                currentFlows = data.flows
                currentGroups = data.groups

                val models = modelFactory.createCellModels(
                    allGroup = data.allGroups,
                    groups = data.groups,
                    allFlows = data.allFlows,
                    flows = data.flows,
                    allRuns = data.allRuns,
                    runs = data.runs
                )

                val topBarState = TopBarState(
                    title = data.group?.name ?: resourceProvider.getString(R.string.groups),
                    isBackVisible = true
                )
                rootViewModel.sendIntent(RootIntent.SetTopBarState(topBarState))

                emit(
                    GroupsState.Data(
                        viewModels = viewModelFactory.createCellViewModels(models, intentProvider)
                    )
                )
            } else {
                emit(
                    GroupsState.Error(
                        message = getFlowsResult.formatError(resourceProvider)
                    )
                )
            }
        }
    }

    private fun createInitialTopBarState(): TopBarState {
        return TopBarState(
            title = resourceProvider.getString(R.string.groups),
            isBackVisible = true
        )
    }
}