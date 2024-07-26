package com.github.aivanovski.testwithme.android.presentation.core.cells.model

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellIntent

sealed interface IconTextCellIntent : BaseCellIntent {
    data class OnClick(
        val cellId: String
    ) : IconTextCellIntent
}