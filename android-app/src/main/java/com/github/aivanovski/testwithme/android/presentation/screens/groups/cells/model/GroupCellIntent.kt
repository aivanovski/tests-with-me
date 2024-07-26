package com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.model

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellIntent

sealed interface GroupCellIntent : BaseCellIntent {

    data class OnClick(
        val cellId: String
    ) : GroupCellIntent

    data class OnDetailsClick(
        val cellId: String
    ) : GroupCellIntent
}