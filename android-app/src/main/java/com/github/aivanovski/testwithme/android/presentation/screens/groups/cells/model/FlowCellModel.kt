package com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.IconTint

data class FlowCellModel(
    override val id: String,
    val icon: ImageVector,
    val iconTint: IconTint,
    val title: String,
    val description: String,
    val chipText: String
) : BaseCellModel(id)