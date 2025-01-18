package com.github.aivanovski.testswithme.android.presentation.core.cells

import kotlinx.coroutines.flow.MutableStateFlow

open class BaseMutableCellViewModel<T : BaseCellModel>(
    initialModel: T
) : CellViewModel {

    val observableModel = MutableStateFlow(initialModel)

    override val model: BaseCellModel
        get() = observableModel.value
}