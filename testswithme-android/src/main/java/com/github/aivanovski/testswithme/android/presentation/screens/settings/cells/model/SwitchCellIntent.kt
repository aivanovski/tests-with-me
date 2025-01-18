package com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

interface SwitchCellIntent : BaseCellIntent {

    data class OnCheckChanged(
        val cellId: String,
        val isChecked: Boolean
    ) : SwitchCellIntent
}