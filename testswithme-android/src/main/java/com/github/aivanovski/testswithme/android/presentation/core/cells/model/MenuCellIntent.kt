package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

sealed interface MenuCellIntent : BaseCellIntent {
    data class OnClick(
        val id: String
    ) : MenuCellIntent
}