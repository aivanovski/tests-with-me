package com.github.aivanovski.testwithme.android.presentation.screens.flow.cells.model

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellIntent

sealed interface HistoryItemCellIntent : BaseCellIntent {
    data class OnItemClick(
        val id: String
    ) : HistoryItemCellIntent
}