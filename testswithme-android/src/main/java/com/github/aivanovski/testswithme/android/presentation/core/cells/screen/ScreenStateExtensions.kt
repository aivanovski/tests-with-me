package com.github.aivanovski.testswithme.android.presentation.core.cells.screen

import com.github.aivanovski.testswithme.android.entity.ErrorMessage

fun ErrorMessage.toScreenState(): TerminalState.Error {
    return TerminalState.Error(
        message = this
    )
}