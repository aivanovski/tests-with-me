package com.github.aivanovski.testwithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel

data class HeaderCellModel(
    override val id: String,
    val title: String,
    val iconText: String?,
    val icon: ImageVector?,
) : BaseCellModel(id)