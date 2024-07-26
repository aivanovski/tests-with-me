package com.github.aivanovski.testwithme.android.presentation.core.cells.model

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellIntent

sealed interface ButtonCellIntent : BaseCellIntent {
    data class OnClick(
        val id: String
    ) : ButtonCellIntent
}