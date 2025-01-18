package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

class TitleWithIconCellModel(
    override val id: String,
    val title: String,
    val icon: ImageVector?,
    val shape: CornersShape
) : BaseCellModel(id)