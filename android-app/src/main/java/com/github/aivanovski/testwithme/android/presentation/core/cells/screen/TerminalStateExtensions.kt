package com.github.aivanovski.testwithme.android.presentation.core.cells.screen

import com.github.aivanovski.testwithme.android.entity.ErrorMessage

fun ErrorMessage.toTerminalState(): TerminalState.Error {
    return TerminalState.Error(
        message = this
    )
}