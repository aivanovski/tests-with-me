package com.github.aivanovski.testwithme.android.presentation.core.cells.model

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel

data class LabeledTextCellModel(
    override val id: String,
    val label: String,
    val text: String,
    val shape: CornersShape
) : BaseCellModel(id)