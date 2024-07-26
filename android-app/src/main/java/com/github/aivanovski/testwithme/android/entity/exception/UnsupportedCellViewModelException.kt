package com.github.aivanovski.testwithme.android.entity.exception

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel

class UnsupportedCellViewModelException(
    viewModel: BaseCellViewModel
) : IllegalArgumentException(
    "Unsupported cell view model type: ${viewModel::class.qualifiedName}"
)