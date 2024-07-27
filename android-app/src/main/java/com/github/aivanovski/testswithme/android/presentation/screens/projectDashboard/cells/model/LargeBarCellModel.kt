package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape

data class LargeBarCellModel(
    override val id: String,
    val progress: Float,
    val title: String,
    val subtitle: String,
    val shape: CornersShape
) : BaseCellModel(id)