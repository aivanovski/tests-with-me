package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.Color
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

class TextCellModel(
    override val id: String,
    val text: String,
    val textSize: TextSize,
    val textColor: Color,
    val shape: CornersShape
) : BaseCellModel(id)