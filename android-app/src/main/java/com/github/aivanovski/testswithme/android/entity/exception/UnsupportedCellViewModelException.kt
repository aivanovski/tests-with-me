package com.github.aivanovski.testswithme.android.entity.exception

import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel

class UnsupportedCellViewModelException(
    viewModel: CellViewModel
) : IllegalArgumentException(
    "Unsupported cell view model type: ${viewModel::class.qualifiedName}"
)