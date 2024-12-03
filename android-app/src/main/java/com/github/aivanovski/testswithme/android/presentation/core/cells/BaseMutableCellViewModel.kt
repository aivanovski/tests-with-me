package com.github.aivanovski.testswithme.android.presentation.core.cells

import kotlinx.coroutines.flow.MutableStateFlow

open class BaseMutableCellViewModel<T : BaseCellModel>(
    initialModel: T
) : CellViewModel {

    val observableModel = MutableStateFlow(initialModel)

    override val model: BaseCellModel
        get() = observableModel.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseMutableCellViewModel<*>

        return observableModel.value == other.observableModel.value
    }

    override fun hashCode(): Int {
        return observableModel.value.hashCode()
    }
}