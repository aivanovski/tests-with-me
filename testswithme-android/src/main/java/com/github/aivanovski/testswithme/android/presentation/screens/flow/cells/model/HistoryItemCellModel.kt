package com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTint

data class HistoryItemCellModel(
    override val id: String,
    val icon: ImageVector,
    val iconTint: IconTint,
    val title: String,
    val description: String
) : BaseCellModel(id)