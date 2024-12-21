package com.github.aivanovski.testswithme.android.presentation.core.cells.screen

import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel

interface CellsScreenState {
    val terminalState: TerminalState?
    val viewModels: List<CellViewModel>
}