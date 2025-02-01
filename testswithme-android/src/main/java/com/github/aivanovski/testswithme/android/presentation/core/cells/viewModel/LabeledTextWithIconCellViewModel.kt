package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextWithIconCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextWithIconCellIntent

@Immutable
class LabeledTextWithIconCellViewModel(
    override val model: LabeledTextWithIconCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: LabeledTextWithIconCellIntent) {
        intentProvider.sendIntent(intent)
    }
}