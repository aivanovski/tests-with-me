package com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

sealed interface TwoTextCellIntent : BaseCellIntent {

    data class OnClick(
        val cellId: String
    ) : TwoTextCellIntent
}