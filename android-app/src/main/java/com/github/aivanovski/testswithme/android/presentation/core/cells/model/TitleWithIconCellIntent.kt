package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

interface TitleWithIconCellIntent : BaseCellIntent {

    data class OnIconClick(
        val cellId: String
    ) : TitleWithIconCellIntent
}