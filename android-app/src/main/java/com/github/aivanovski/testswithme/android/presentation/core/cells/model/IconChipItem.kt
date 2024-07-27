package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class IconChipItem(
    val icon: ImageVector,
    val iconTint: IconTint,
    val text: String,
    val textColor: Color,
    val chipColor: Color
)