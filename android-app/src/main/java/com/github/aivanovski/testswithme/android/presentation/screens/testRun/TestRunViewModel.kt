package com.github.aivanovski.testswithme.android.presentation.screens.testRun

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.toScreenState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.TestRunCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.model.TestRunIntent
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.model.TestRunScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.model.TestRunScreenData
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.model.TestRunState
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestScreenArgs
import com.github.aivanovski.testswithme.android.utils.formatError
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TestRunViewModel(
    private val interactor: TestRunInteractor,
    private val cellFactory: TestRunCellFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: TestRunScreenArgs
) : BaseViewModel() {

    val state = MutableStateFlow(newInitialState())
    private val intents = Channel<TestRunIntent>()
    private var isSubscribed = false
    private val data = MutableStateFlow<TestRunScreenData?>(null)

    override fun start() {
        super.start()

        rootViewModel.sendIntent(RootIntent.SetTopBarState(createTopBarState()))

        if (!isSubscribed) {
            isSubscribed = true

            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(TestRunIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent, state.value) }
                    .flowOn(Dispatchers.IO)
                    .collect { newState ->
                        state.value = newState
                    }
            }

            viewModelScope.launch {
                interactor.isLoggedInFlow()
                    .collect {
                        intents.trySend(TestRunIntent.ReloadData)
                    }
            }
        }
    }

    fun sendIntent(intent: TestRunIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(
        intent: TestRunIntent,
        state: TestRunState
    ): Flow<TestRunState> {
        return when (intent) {
            TestRunIntent.Initialize -> loadData()
            TestRunIntent.ReloadData -> loadData()
            TestRunIntent.OnFabClick -> {
                navigateToUploadTestScreen()
                emptyFlow()
            }
        }
    }

    private fun loadData(): Flow<TestRunState> {
        return flow {
            emit(TestRunState(terminalState = TerminalState.Loading))

            val loadDataResult = interactor.loadData(args.jobUid)
            if (loadDataResult.isLeft()) {
                val terminalState = loadDataResult
                    .formatError(resourceProvider)
                    .toScreenState()

                emit(TestRunState(terminalState = terminalState))
                return@flow
            }

            data.value = loadDataResult.unwrap()
            val data = loadDataResult.unwrap()
            val viewModels = cellFactory.createCellViewModels(
                data = data,
                intentProvider = intentProvider
            )

            emit(
                TestRunState(
                    viewModels = viewModels,
                    isAddButtonVisible = interactor.isLoggedIn()
                )
            )
        }
    }

    private fun navigateToUploadTestScreen() {
        val data = this.data.value ?: return

        router.navigateTo(
            Screen.UploadTest(
                UploadTestScreenArgs(
                    flowUid = data.flow.uid
                )
            )
        )
    }

    private fun newInitialState(): TestRunState {
        return TestRunState(
            terminalState = TerminalState.Loading,
            viewModels = emptyList(),
            isAddButtonVisible = interactor.isLoggedIn()
        )
    }

    private fun createTopBarState(): TopBarState {
        return TopBarState(
            title = args.screenTitle ?: StringUtils.EMPTY, // TODO: load title if need
            isBackVisible = true
        )
    }
}