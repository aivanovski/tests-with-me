package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TitleWithIconCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TitleWithIconCellModel

@Immutable
class TitleWithIconCellViewModel(
    override val model: TitleWithIconCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: TitleWithIconCellIntent) {
        intentProvider.sendIntent(intent)
    }
}