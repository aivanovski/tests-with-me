package com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.viewModel

import com.github.aivanovski.testwithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.model.ProjectCellIntent
import com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.model.ProjectCellModel

class ProjectCellViewModel(
    override val model: ProjectCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: ProjectCellIntent) {
        intentProvider.sendIntent(intent)
    }
}