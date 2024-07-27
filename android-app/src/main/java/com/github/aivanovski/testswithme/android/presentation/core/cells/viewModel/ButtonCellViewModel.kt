package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ButtonCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ButtonCellModel

@Immutable
class ButtonCellViewModel(
    override val model: ButtonCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: ButtonCellIntent) {
        intentProvider.sendIntent(intent)
    }
}