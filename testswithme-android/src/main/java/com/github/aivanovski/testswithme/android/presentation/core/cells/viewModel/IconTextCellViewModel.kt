package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTextCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTextCellModel

class IconTextCellViewModel(
    override val model: IconTextCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: IconTextCellIntent) {
        intentProvider.sendIntent(intent)
    }
}