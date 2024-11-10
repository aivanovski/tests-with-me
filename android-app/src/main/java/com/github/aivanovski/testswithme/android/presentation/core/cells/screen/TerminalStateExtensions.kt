package com.github.aivanovski.testswithme.android.presentation.core.cells.screen

import com.github.aivanovski.testswithme.android.entity.ErrorMessage

fun ErrorMessage.toTerminalState(): ScreenState.Error {
    return ScreenState.Error(
        message = this
    )
}