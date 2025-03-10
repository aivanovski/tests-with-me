package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class UnshapedChipRowCellModel(
    override val id: String,
    val chips: List<TextChipItem>
) : BaseCellModel(id)