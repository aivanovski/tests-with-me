package com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

sealed interface HistoryItemCellIntent : BaseCellIntent {
    data class OnItemClick(
        val cellId: String
    ) : HistoryItemCellIntent
}