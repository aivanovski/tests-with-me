package com.github.aivanovski.testswithme.android.presentation.core.cells.model

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

data class HeaderWithDescriptionCellModel(
    override val id: String,
    val title: String,
    val description: String
) : BaseCellModel(id)