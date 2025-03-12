package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTableCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTableCellModel

@Immutable
class LabeledTableCellViewModel(
    override val model: LabeledTableCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: LabeledTableCellIntent) {
        intentProvider.sendIntent(intent)
    }
}