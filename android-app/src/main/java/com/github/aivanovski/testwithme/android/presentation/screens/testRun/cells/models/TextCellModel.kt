package com.github.aivanovski.testwithme.android.presentation.screens.testRun.cells.models

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel

data class TextCellModel(
    override val id: String,
    val text: String
) : BaseCellModel(id)