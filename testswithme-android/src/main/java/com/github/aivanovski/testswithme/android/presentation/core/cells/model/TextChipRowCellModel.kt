package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class TextChipRowCellModel(
    override val id: String,
    val chips: List<TextChipItem>,
    val shape: CornersShape
) : BaseCellModel(id)