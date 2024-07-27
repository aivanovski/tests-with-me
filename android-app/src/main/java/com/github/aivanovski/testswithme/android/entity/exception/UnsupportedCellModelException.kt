package com.github.aivanovski.testswithme.android.entity.exception

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel

class UnsupportedCellModelException(
    model: BaseCellModel
) : IllegalArgumentException(
    "Unsupported model type: ${model::class.qualifiedName}"
)