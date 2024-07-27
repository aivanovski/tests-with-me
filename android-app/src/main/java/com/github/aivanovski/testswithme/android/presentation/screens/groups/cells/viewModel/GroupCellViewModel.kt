package com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel

import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.GroupCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.GroupCellModel

class GroupCellViewModel(
    override val model: GroupCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: GroupCellIntent) {
        intentProvider.sendIntent(intent)
    }
}