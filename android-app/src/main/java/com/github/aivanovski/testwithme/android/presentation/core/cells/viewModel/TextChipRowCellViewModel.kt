package com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testwithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TextChipRowCellIntent
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TextChipRowCellModel

@Immutable
class TextChipRowCellViewModel(
    override val model: TextChipRowCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: TextChipRowCellIntent) {
        intentProvider.sendIntent(intent)
    }
}