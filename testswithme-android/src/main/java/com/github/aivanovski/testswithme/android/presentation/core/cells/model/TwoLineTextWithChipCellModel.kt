package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.Color
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class TwoLineTextWithChipCellModel(
    override val id: String,
    val title: String,
    val description: String,
    val chipText: String,
    val chipTextColor: Color,
    val chipColor: Color,
    val shape: CornersShape
) : BaseCellModel(id)