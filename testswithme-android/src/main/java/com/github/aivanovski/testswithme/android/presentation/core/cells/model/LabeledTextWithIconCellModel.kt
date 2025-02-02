package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class LabeledTextWithIconCellModel(
    override val id: String,
    val label: String,
    val text: String,
    val icon: ImageVector?,
    val shape: CornersShape
) : BaseCellModel(id)