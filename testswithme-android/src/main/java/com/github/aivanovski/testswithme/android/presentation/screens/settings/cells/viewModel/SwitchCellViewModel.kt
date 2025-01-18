package com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel

import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseMutableCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.SwitchCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.SwitchCellModel

class SwitchCellViewModel(
    model: SwitchCellModel,
    private val intentProvider: CellIntentProvider
) : BaseMutableCellViewModel<SwitchCellModel>(model) {

    fun sendIntent(intent: SwitchCellIntent) {
        when (intent) {
            is SwitchCellIntent.OnCheckChanged -> {
                observableModel.value = observableModel.value.copy(
                    isChecked = intent.isChecked
                )
            }
        }

        intentProvider.sendIntent(intent)
    }
}