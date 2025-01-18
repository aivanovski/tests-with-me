package com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.TwoTextCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.TwoTextCellModel

@Immutable
class TwoTextCellViewModel(
    override val model: TwoTextCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: TwoTextCellIntent) {
        intentProvider.sendIntent(intent)
    }
}