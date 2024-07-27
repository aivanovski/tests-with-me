package com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

sealed interface FlowCellIntent : BaseCellIntent {

    data class OnClick(
        val cellId: String
    ) : FlowCellIntent
}