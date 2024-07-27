package com.github.aivanovski.testswithme.android.presentation.screens.groups.cells

import androidx.compose.runtime.Composable
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.SpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.ui.FlowCell
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.ui.GroupCell
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.FlowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.GroupCellViewModel

class GroupsUiCellFactory {

    @Composable
    fun createCell(viewModel: BaseCellViewModel) {
        when (viewModel) {
            is FlowCellViewModel -> FlowCell(viewModel)
            is GroupCellViewModel -> GroupCell(viewModel)
            is SpaceCellViewModel -> SpaceCell(viewModel)
            else -> throw IllegalStateException()
        }
    }
}