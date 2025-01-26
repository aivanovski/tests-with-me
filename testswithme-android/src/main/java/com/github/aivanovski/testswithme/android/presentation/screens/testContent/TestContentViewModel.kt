package com.github.aivanovski.testswithme.android.presentation.screens.testContent

import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.MviViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetBottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.cells.TestContentCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.cells.TestContentCellFactory.CellId
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentArgs
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentData
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentIntent
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentState
import com.github.aivanovski.testswithme.android.presentation.screens.textViewer.model.TextViewerArgs
import com.github.aivanovski.testswithme.android.utils.toTerminalState
import com.github.aivanovski.testswithme.extensions.unwrap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class TestContentViewModel(
    private val interactor: TestContentInteractor,
    private val cellFactory: TestContentCellFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: TestContentArgs
) : MviViewModel<TestContentState, TestContentIntent>(
    initialState = TestContentState(TerminalState.Loading),
    initialIntent = TestContentIntent.Initialize
) {

    private var data = MutableStateFlow<TestContentData?>(null)

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBar()))

        val bottomBarState = rootViewModel.bottomBarState.value.copy(isVisible = true)
        rootViewModel.sendIntent(SetBottomBarState(bottomBarState))
    }

    override fun handleIntent(intent: TestContentIntent): Flow<TestContentState> =
        when (intent) {
            TestContentIntent.Initialize -> loadData()
        }

    override fun handleCellIntent(intent: BaseCellIntent) {
        when {
            intent is HeaderCellIntent.OnIconClick && intent.cellId == CellId.REPORT_HEADER ->
                navigateToTestReportScreen()

            intent is HeaderCellIntent.OnIconClick && intent.cellId == CellId.ERROR_HEADER ->
                navigateToErrorScreen()
        }
    }

    private fun loadData(): Flow<TestContentState> {
        return flow {
            emit(TestContentState(terminalState = TerminalState.Loading))

            val loadDataResult = interactor.loadData(
                flowUid = args.flowUid,
                mode = args.mode
            )
            if (loadDataResult.isLeft()) {
                emit(TestContentState(loadDataResult.toTerminalState(resourceProvider)))
                return@flow
            }

            data.value = loadDataResult.unwrap()

            val viewModels = cellFactory.createCellViewModels(
                data = loadDataResult.unwrap(),
                mode = args.mode,
                intentProvider = intentProvider
            )

            emit(TestContentState(viewModels = viewModels))
        }
    }

    private fun navigateToTestReportScreen() {
        val report = this.data.value?.report ?: return

        router.navigateTo(
            Screen.TextViewer(
                TextViewerArgs(
                    screenTitle = resourceProvider.getString(R.string.test_report),
                    content = report
                )
            )
        )
    }

    private fun navigateToErrorScreen() {
        val stacktrace = this.data.value?.parsedReport?.stacktrace ?: return

        router.navigateTo(
            Screen.TextViewer(
                TextViewerArgs(
                    screenTitle = resourceProvider.getString(R.string.error_message_with_colon),
                    content = stacktrace
                )
            )
        )
    }

    private fun createTopBar(): TopBarState =
        TopBarState(
            title = args.screenTitle,
            isBackVisible = true
        )
}