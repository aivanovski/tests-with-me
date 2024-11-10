package com.github.aivanovski.testswithme.android.presentation.core.cells.screen

import com.github.aivanovski.testswithme.android.entity.ErrorMessage

sealed interface ScreenState {

    data object Loading : ScreenState

    data class Empty(
        val message: String
    ) : ScreenState

    data class Error(
        val message: ErrorMessage
    ) : ScreenState
}