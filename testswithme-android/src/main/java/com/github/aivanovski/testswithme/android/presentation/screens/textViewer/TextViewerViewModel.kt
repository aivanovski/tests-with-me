package com.github.aivanovski.testswithme.android.presentation.screens.textViewer

import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.MviViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.textViewer.cells.TextViewerCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.textViewer.model.TextViewerArgs
import com.github.aivanovski.testswithme.android.presentation.screens.textViewer.model.TextViewerIntent
import com.github.aivanovski.testswithme.android.presentation.screens.textViewer.model.TextViewerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TextViewerViewModel(
    private val cellFactory: TextViewerCellFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: TextViewerArgs
) : MviViewModel<TextViewerState, TextViewerIntent>(
    initialState = TextViewerState(terminalState = TerminalState.Loading),
    initialIntent = TextViewerIntent.Initialize
) {

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))

        val bottomBarState = rootViewModel.bottomBarState.value.copy(isVisible = true)
        rootViewModel.sendIntent(RootIntent.SetBottomBarState(bottomBarState))
    }

    override fun handleIntent(intent: TextViewerIntent): Flow<TextViewerState> =
        when (intent) {
            TextViewerIntent.Initialize -> loadData()
        }

    private fun loadData(): Flow<TextViewerState> {
        return flow {
            emit(TextViewerState(terminalState = TerminalState.Loading))

            val viewModels = cellFactory.createCellViewModels(
                content = args.content,
                intentProvider = intentProvider
            )

            emit(TextViewerState(viewModels = viewModels))
        }
    }

    private fun createTopBarState(): TopBarState =
        TopBarState(
            title = args.screenTitle,
            isBackVisible = true
        )
}