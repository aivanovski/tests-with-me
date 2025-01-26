package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TwoLineTextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TwoLineWithChipCellIntent

@Immutable
class TwoLineWithChipCellViewModel(
    override val model: TwoLineTextWithChipCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: TwoLineWithChipCellIntent) {
        intentProvider.sendIntent(intent)
    }
}