package com.github.aivanovski.testswithme.android.presentation.screens.projects.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

interface ProjectCellIntent : BaseCellIntent {
    data class OnClick(
        val id: String
    ) : ProjectCellIntent
}