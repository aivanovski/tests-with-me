package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class UnshapedTextCellModel(
    override val id: String,
    val text: String,
    val textSize: TextSize
) : BaseCellModel(id)