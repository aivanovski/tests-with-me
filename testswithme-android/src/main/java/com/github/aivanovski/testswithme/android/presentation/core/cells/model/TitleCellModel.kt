package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

class TitleCellModel(
    override val id: String,
    val title: String,
    val shape: CornersShape
) : BaseCellModel(id)