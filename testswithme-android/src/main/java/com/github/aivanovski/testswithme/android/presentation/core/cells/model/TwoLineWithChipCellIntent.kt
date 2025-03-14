package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

interface TwoLineWithChipCellIntent : BaseCellIntent {

    data class OnClick(
        val cellId: String
    ) : TwoLineWithChipCellIntent
}