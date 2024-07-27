package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

sealed interface IconTextCellIntent : BaseCellIntent {
    data class OnClick(
        val cellId: String
    ) : IconTextCellIntent
}