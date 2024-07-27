package com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellModel

@Immutable
class FlowCellViewModel(
    override val model: FlowCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: FlowCellIntent) {
        intentProvider.sendIntent(intent)
    }
}