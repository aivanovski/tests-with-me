package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class LabeledTableCellModel(
    override val id: String,
    val labels: List<String>,
    val values: List<String>,
    val shape: CornersShape
) : BaseCellModel(id)