package com.github.aivanovski.testswithme.android.presentation.screens.testReport

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.toScreenState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.testReport.cells.TestReportCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.testReport.model.TestReportIntent
import com.github.aivanovski.testswithme.android.presentation.screens.testReport.model.TestReportScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.testReport.model.TestReportState
import com.github.aivanovski.testswithme.android.utils.formatError
import com.github.aivanovski.testswithme.extensions.unwrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TestReportViewModel(
    private val interactor: TestReportInteractor,
    private val cellFactory: TestReportCellFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: TestReportScreenArgs
) : BaseViewModel() {

    val state = MutableStateFlow(TestReportState(terminalState = TerminalState.Loading))
    private val intents = Channel<TestReportIntent>()

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))

        doOnceWhenStarted {
            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(TestReportIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent) }
                    .flowOn(Dispatchers.IO)
                    .collect { newState ->
                        state.value = newState
                    }
            }
        }
    }

    fun sendIntent(intent: TestReportIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(intent: TestReportIntent): Flow<TestReportState> {
        return when (intent) {
            TestReportIntent.Initialize -> loadData()
        }
    }

    private fun loadData(): Flow<TestReportState> {
        return flow {
            emit(TestReportState(terminalState = TerminalState.Loading))

            val loadDataResult = interactor.loadData(args.flowRunUid)
            if (loadDataResult.isLeft()) {
                val terminalState = loadDataResult
                    .formatError(resourceProvider)
                    .toScreenState()

                emit(TestReportState(terminalState = terminalState))
                return@flow
            }

            val runWithReport = loadDataResult.unwrap()
            val cellViewModels = cellFactory.createCellViewModels(
                flowRunWithReport = runWithReport,
                intentProvider = intentProvider
            )

            emit(TestReportState(viewModels = cellViewModels))
        }
    }

    private fun createTopBarState(): TopBarState =
        TopBarState(
            title = resourceProvider.getString(R.string.report),
            isBackVisible = true
        )
}