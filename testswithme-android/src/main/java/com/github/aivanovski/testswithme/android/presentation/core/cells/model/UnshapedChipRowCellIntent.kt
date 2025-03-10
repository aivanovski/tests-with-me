package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

interface UnshapedChipRowCellIntent : BaseCellIntent {

    data class OnClick(
        val chipIndex: Int
    ) : UnshapedChipRowCellIntent
}