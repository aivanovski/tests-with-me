package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class MenuCellModel(
    override val id: String,
    val icon: ImageVector,
    val title: String
) : BaseCellModel(id)