package com.github.aivanovski.testwithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel

class IconTextCellModel(
    override val id: String,
    val title: String,
    val icon: ImageVector,
    val iconTint: IconTint
) : BaseCellModel(id)