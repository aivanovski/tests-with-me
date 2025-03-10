package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.UnshapedChipRowCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.UnshapedChipRowCellModel

@Immutable
class UnshapedChipRowCellViewModel(
    override val model: UnshapedChipRowCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: UnshapedChipRowCellIntent) {
        intentProvider.sendIntent(intent)
    }
}