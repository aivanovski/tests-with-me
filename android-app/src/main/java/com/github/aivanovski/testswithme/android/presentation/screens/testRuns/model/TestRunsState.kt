package com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreenState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState

@Immutable
data class TestRunsState(
    override val terminalState: TerminalState? = null,
    override val viewModels: List<CellViewModel> = emptyList()
) : CellsScreenState