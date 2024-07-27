package com.github.aivanovski.testswithme.android.presentation.core.cells.screen

import com.github.aivanovski.testswithme.android.entity.ErrorMessage

sealed interface TerminalState {

    object Loading : TerminalState

    data class Empty(
        val message: String
    ) : TerminalState

    data class Error(
        val message: ErrorMessage
    ) : TerminalState
}