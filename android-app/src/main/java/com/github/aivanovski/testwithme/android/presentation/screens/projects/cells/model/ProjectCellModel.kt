package com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.model

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel

data class ProjectCellModel(
    override val id: String,
    val title: String,
    val description: String,
    val iconUrl: String?
) : BaseCellModel(id)