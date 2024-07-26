package com.github.aivanovski.testwithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.Color

data class TextChipItem(
    val text: String,
    val textColor: Color,
    val textSize: TextSize,
    val isClickable: Boolean,
    val isSelected: Boolean
)