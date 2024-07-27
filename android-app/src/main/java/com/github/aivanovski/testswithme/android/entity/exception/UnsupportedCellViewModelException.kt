package com.github.aivanovski.testswithme.android.entity.exception

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel

class UnsupportedCellViewModelException(
    viewModel: BaseCellViewModel
) : IllegalArgumentException(
    "Unsupported cell view model type: ${viewModel::class.qualifiedName}"
)