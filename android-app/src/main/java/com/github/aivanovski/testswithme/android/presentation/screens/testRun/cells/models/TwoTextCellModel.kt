package com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.models

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class TwoTextCellModel(
    override val id: String,
    val title: String,
    val description: String
) : BaseCellModel(id)