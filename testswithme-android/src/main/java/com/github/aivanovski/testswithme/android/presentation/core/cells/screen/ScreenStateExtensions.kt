package com.github.aivanovski.testswithme.android.presentation.core.cells.screen

import com.github.aivanovski.testswithme.android.entity.ErrorMessage

fun ErrorMessage.toTerminalState(): TerminalState.Error {
    return TerminalState.Error(
        message = this
    )
}

fun CellsScreenState.isLoading(): Boolean = this.terminalState == TerminalState.Loading