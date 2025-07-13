package com.github.aivanovski.testswithme.android.presentation.screens.testRuns

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.presentation.core.CellsMviViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconThreeTextCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.isLoading
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.toTerminalState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenMode
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.BottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetBottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.cells.TestRunsCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model.TestRunsData
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model.TestRunsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model.TestRunsState
import com.github.aivanovski.testswithme.android.utils.formatError
import com.github.aivanovski.testswithme.extensions.unwrap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TestRunsViewModel(
    private val interactor: TestRunsInteractor,
    private val cellFactory: TestRunsCellFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router
) : CellsMviViewModel<TestRunsState, TestRunsIntent>(
    initialState = TestRunsState(terminalState = TerminalState.Loading),
    initialIntent = TestRunsIntent.Initialize
) {

    private val data = MutableStateFlow<TestRunsData?>(null)

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createInitialTopBarState()))
        rootViewModel.sendIntent(SetBottomBarState(createBottomBarState()))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))

        doOnceWhenStarted {
            viewModelScope.launch {
                interactor.isLoggedInFlow()
                    .collect {
                        sendIntent(TestRunsIntent.ReloadData)
                    }
            }
        }

        if (!state.value.isLoading()) {
            sendIntent(TestRunsIntent.ReloadData)
        }
    }

    override fun handleCellIntent(intent: BaseCellIntent) {
        when (intent) {
            is IconThreeTextCellIntent.OnClick -> navigateToTestRunScreen(jobUid = intent.id)
        }
    }

    override fun handleIntent(intent: TestRunsIntent): Flow<TestRunsState> {
        return when (intent) {
            TestRunsIntent.Initialize -> loadData()
            TestRunsIntent.ReloadData -> loadData()
        }
    }

    private fun navigateToTestRunScreen(jobUid: String) {
        val data = data.value ?: return

        val job = data.jobHistory
            .firstOrNull { job -> job.uid == jobUid }
            ?: return

        val flow = data.allFlows
            .firstOrNull { flow -> flow.entry.uid == job.flowUid }
            ?: return

        val sourceType = flow.entry.sourceType

        val mode = if (sourceType == SourceType.REMOTE && interactor.isLoggedIn()) {
            FlowScreenMode.Flow(flow.entry.uid)
        } else {
            FlowScreenMode.LocalFlow(flow.entry.uid)
        }

        router.navigateTo(
            Screen.Flow(
                FlowScreenArgs(
                    mode = mode,
                    screenTitle = flow.entry.name
                )
            )
        )
    }

    private fun loadData(): Flow<TestRunsState> {
        return interactor.loadData()
            .map { loadDataResult ->
                if (loadDataResult.isLeft()) {
                    val terminalState = loadDataResult
                        .formatError(resourceProvider)
                        .toTerminalState()

                    return@map TestRunsState(terminalState = terminalState)
                }

                data.value = loadDataResult.unwrap()
                val data = loadDataResult.unwrap()

                if (data.localRuns.isNotEmpty()) {
                    val viewModels = cellFactory.createCellViewModels(
                        data = data,
                        intentProvider = intentProvider
                    )
                    TestRunsState(viewModels = viewModels)
                } else {
                    val message = resourceProvider.getString(R.string.no_tests)
                    TestRunsState(terminalState = TerminalState.Empty(message))
                }
            }
            .onStart { emit(TestRunsState(terminalState = TerminalState.Loading)) }
    }

    private fun createInitialTopBarState(): TopBarState {
        return TopBarState(
            title = resourceProvider.getString(R.string.test_runs),
            isBackVisible = false
        )
    }

    private fun createBottomBarState(): BottomBarState {
        return rootViewModel.bottomBarState.value.copy(
            isVisible = true,
            selectedIndex = 1
        )
    }
}