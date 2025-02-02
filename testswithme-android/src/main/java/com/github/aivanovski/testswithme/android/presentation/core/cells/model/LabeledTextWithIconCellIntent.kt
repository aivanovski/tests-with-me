package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

interface LabeledTextWithIconCellIntent : BaseCellIntent {

    data class OnIconClick(
        val cellId: String
    ) : LabeledTextWithIconCellIntent
}