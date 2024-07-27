package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class IconThreeTextCellModel(
    override val id: String,
    val title: String,
    val description: String,
    val secondaryDescription: String,
    val icon: ImageVector,
    val iconTint: IconTint
) : BaseCellModel(id)