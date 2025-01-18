package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

sealed interface HeaderCellIntent : BaseCellIntent {
    data class OnIconClick(
        val cellId: String
    ) : HeaderCellIntent
}