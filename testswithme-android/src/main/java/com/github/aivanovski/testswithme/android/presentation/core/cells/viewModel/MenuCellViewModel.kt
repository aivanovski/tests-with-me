package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.MenuCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.MenuCellModel

@Immutable
class MenuCellViewModel(
    override val model: MenuCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: MenuCellIntent) {
        intentProvider.sendIntent(intent)
    }
}