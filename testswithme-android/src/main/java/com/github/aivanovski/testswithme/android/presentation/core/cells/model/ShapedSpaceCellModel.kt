package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.unit.Dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

class ShapedSpaceCellModel(
    override val id: String,
    val height: Dp,
    val shape: CornersShape
) : BaseCellModel(id)