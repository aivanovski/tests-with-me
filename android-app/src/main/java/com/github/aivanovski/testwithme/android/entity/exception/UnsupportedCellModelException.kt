package com.github.aivanovski.testwithme.android.entity.exception

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel

class UnsupportedCellModelException(
    model: BaseCellModel
) : IllegalArgumentException(
    "Unsupported model type: ${model::class.qualifiedName}"
)