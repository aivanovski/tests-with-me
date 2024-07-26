package com.github.aivanovski.testwithme.android.presentation.core.cells.model

import androidx.compose.ui.unit.Dp
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel

class ShapedSpaceCellModel(
    val height: Dp,
    val shape: CornersShape
) : BaseCellModel(null)