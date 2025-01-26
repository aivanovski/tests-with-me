package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class TextButtonCellModel(
    override val id: String,
    val text: String,
    val shape: CornersShape
) : BaseCellModel(id)