package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconThreeTextCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconThreeTextCellModel

@Immutable
class IconThreeTextCellViewModel(
    override val model: IconThreeTextCellModel,
    private val intentProvider: CellIntentProvider
) : BaseCellViewModel(model) {

    fun sendIntent(intent: IconThreeTextCellIntent) {
        intentProvider.sendIntent(intent)
    }
}