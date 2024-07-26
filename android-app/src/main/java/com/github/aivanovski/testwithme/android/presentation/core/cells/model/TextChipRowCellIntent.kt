package com.github.aivanovski.testwithme.android.presentation.core.cells.model

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellIntent

sealed interface TextChipRowCellIntent : BaseCellIntent {
    data class OnClick(
        val chipIndex: Int
    ) : TextChipRowCellIntent
}