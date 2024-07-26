package com.github.aivanovski.testwithme.android.presentation.screens.testRun.cells.models

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel

data class TwoTextCellModel(
    override val id: String,
    val title: String,
    val description: String
) : BaseCellModel(id)