package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextButtonCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextButtonCellModel

@Immutable
class TextButtonCellViewModel(
    override val model: TextButtonCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: TextButtonCellIntent) {
        intentProvider.sendIntent(intent)
    }
}