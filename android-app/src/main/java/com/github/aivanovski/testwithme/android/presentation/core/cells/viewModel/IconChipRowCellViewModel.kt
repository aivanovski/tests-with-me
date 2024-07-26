package com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.IconChipRowCellModel

@Immutable
class IconChipRowCellViewModel(
    override val model: IconChipRowCellModel
) : BaseCellViewModel(model)