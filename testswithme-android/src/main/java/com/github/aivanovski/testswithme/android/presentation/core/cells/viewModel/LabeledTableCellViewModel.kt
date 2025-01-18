package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTableCellModel

@Immutable
class LabeledTableCellViewModel(
    override val model: LabeledTableCellModel
) : BaseCellViewModel(model)