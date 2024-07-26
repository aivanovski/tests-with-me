package com.github.aivanovski.testwithme.android.presentation.core.cells.model

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel

data class IconChipRowCellModel(
    override val id: String,
    val chips: List<IconChipItem>,
    val shape: CornersShape
) : BaseCellModel(id)