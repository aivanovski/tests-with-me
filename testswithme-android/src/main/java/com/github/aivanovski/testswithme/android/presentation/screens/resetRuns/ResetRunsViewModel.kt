package com.github.aivanovski.testswithme.android.presentation.screens.resetRuns

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.toTerminalState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model.ResetRunsData
import com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model.ResetRunsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model.ResetRunsScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model.ResetRunsState
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.utils.formatErrorMessage
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ResetRunsViewModel(
    private val interactor: ResetRunsInteractor,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: ResetRunsScreenArgs
) : BaseViewModel() {

    val state = MutableStateFlow(ResetRunsState())
    private val intents = Channel<ResetRunsIntent>()
    private val data = MutableStateFlow<ResetRunsData?>(null)

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))

        doOnceWhenStarted {
            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(ResetRunsIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent) }
                    .flowOn(Dispatchers.IO)
                    .collect { newState ->
                        state.value = newState
                    }
            }
        }
    }

    fun sendIntent(intent: ResetRunsIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(intent: ResetRunsIntent): Flow<ResetRunsState> {
        return when (intent) {
            ResetRunsIntent.Initialize -> loadData()
            ResetRunsIntent.OnResetButtonClick -> onResetButtonClicked()
            is ResetRunsIntent.OnVersionSelected -> onVersionSelected(intent)
        }
    }

    private fun loadData(): Flow<ResetRunsState> {
        return flow {
            emit(ResetRunsState(terminalState = TerminalState.Loading))

            val loadDataResult = interactor.loadData(args.projectUid)
            if (loadDataResult.isLeft()) {
                val terminalState = loadDataResult.unwrapError()
                    .formatErrorMessage(resourceProvider)
                    .toTerminalState()

                emit(ResetRunsState(terminalState = terminalState))
                return@flow
            }

            data.value = loadDataResult.unwrap()
            val data = loadDataResult.unwrap()

            emit(
                ResetRunsState(
                    selectedVersion = data.versionNames.firstOrNull() ?: StringUtils.EMPTY,
                    versions = data.versionNames
                )
            )
        }
    }

    private fun onResetButtonClicked(): Flow<ResetRunsState> {
        val state = state.value

        return flow {
            emit(state.copy(terminalState = TerminalState.Loading))

            val resetRunsResult = interactor.resetRuns(
                projectUid = args.projectUid,
                versionName = state.selectedVersion
            )
            if (resetRunsResult.isLeft()) {
                val terminalState = resetRunsResult.unwrapError()
                    .formatErrorMessage(resourceProvider)
                    .toTerminalState()

                emit(state.copy(terminalState = terminalState))
                return@flow
            }

            router.exit()
        }
    }

    private fun onVersionSelected(intent: ResetRunsIntent.OnVersionSelected): Flow<ResetRunsState> {
        return flowOf(
            state.value.copy(
                selectedVersion = intent.versionName
            )
        )
    }

    private fun createTopBarState(): TopBarState =
        TopBarState(
            title = resourceProvider.getString(R.string.reset_progress),
            isBackVisible = true
        )
}