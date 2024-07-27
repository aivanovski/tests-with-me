package com.github.aivanovski.testswithme.android.presentation.screens.groups.cells

import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModelFactory
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.GroupCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.FlowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.GroupCellViewModel

class GroupsCellViewModelFactory : CellViewModelFactory {

    override fun createCellViewModel(
        model: BaseCellModel,
        intentProvider: CellIntentProvider
    ): BaseCellViewModel {
        return when (model) {
            is FlowCellModel -> FlowCellViewModel(model, intentProvider)
            is GroupCellModel -> GroupCellViewModel(model, intentProvider)
            is SpaceCellModel -> SpaceCellViewModel(model)
            else -> throwUnsupportedModelException(model)
        }
    }
}