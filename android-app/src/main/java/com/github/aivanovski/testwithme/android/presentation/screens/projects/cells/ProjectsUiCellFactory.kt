package com.github.aivanovski.testwithme.android.presentation.screens.projects.cells

import androidx.compose.runtime.Composable
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.ui.SpaceCell
import com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel.SpaceCellViewModel

class ProjectsUiCellFactory {

    @Composable
    fun createCell(viewModel: BaseCellViewModel) {
        when (viewModel) {
            is SpaceCellViewModel -> SpaceCell(viewModel)
            else -> throw IllegalStateException()
        }
    }
}