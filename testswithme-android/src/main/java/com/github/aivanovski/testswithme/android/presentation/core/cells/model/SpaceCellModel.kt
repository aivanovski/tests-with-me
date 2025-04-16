package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import androidx.compose.ui.unit.Dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class SpaceCellModel(
    override val id: String,
    val height: Dp
) : BaseCellModel(id)