package com.github.aivanovski.testwithme.android.presentation.core.cells.screen

import com.github.aivanovski.testwithme.android.entity.ErrorMessage

sealed interface TerminalState {

    object Loading : TerminalState

    data class Empty(
        val message: String
    ) : TerminalState

    data class Error(
        val message: ErrorMessage
    ) : TerminalState
}