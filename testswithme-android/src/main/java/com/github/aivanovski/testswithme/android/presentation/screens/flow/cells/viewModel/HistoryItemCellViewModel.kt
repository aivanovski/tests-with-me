package com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.model.HistoryItemCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.model.HistoryItemCellModel

@Immutable
class HistoryItemCellViewModel(
    override val model: HistoryItemCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: HistoryItemCellIntent) {
        intentProvider.sendIntent(intent)
    }
}