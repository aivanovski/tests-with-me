package com.github.aivanovski.testswithme.android.presentation.core.cells

open class BaseCellViewModel(
    open val model: BaseCellModel
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseCellViewModel

        return model == other.model
    }

    override fun hashCode(): Int {
        return model.hashCode()
    }
}