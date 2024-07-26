package com.github.aivanovski.testwithme.android.presentation.core.cells

import com.github.aivanovski.testwithme.android.presentation.core.CellIntentProvider

@Deprecated("")
interface CellViewModelFactory {

    fun createCellViewModels(
        models: List<BaseCellModel>,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return models.map { model -> createCellViewModel(model, intentProvider) }
    }

    fun createCellViewModel(
        model: BaseCellModel,
        intentProvider: CellIntentProvider
    ): BaseCellViewModel

    fun throwUnsupportedModelException(model: BaseCellModel): Nothing {
        throw IllegalArgumentException(
            "Unable to find ViewModel for model: ${model::class.qualifiedName}"
        )
    }
}