package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

interface LabeledTableCellIntent : BaseCellIntent {

    data class OnColumnClick(
        val cellId: String,
        val columnIndex: Int
    ) : LabeledTableCellIntent
}