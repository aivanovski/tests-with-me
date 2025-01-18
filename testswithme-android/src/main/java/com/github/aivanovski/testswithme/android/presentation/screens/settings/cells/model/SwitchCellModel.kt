package com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class SwitchCellModel(
    override val id: String,
    val title: String,
    val description: String,
    val isChecked: Boolean,
    val isEnabled: Boolean
) : BaseCellModel(id)