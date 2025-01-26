package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.Color
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class TextWithChipCellModel(
    override val id: String,
    val text: String,
    val textSize: TextSize,
    val chipText: String,
    val chipTextColor: Color,
    val chipColor: Color,
    val shape: CornersShape
) : BaseCellModel(id)