package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class TextCellModel(
    override val id: String,
    val text: String
) : BaseCellModel(id)