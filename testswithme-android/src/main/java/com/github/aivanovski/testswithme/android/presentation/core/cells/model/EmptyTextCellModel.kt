package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class EmptyTextCellModel(
    override val id: String,
    val message: String
) : BaseCellModel(id)