package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class DividerCellModel(
    override val id: String,
    val padding: Dp,
    val color: Color
) : BaseCellModel(id)