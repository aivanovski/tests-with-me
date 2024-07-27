package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.unit.Dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

class ShapedSpaceCellModel(
    val height: Dp,
    val shape: CornersShape
) : BaseCellModel(null)