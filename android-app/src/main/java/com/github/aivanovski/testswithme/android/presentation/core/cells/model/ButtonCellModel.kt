package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.graphics.Color
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class ButtonCellModel(
    override val id: String,
    val text: String,
    val isButtonEnabled: Boolean,
    val buttonColor: Color,
    val shape: CornersShape
) : BaseCellModel(id)