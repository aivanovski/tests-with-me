package com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.LabeledTextCellModel

@Immutable
class LabeledTextCellViewModel(
    override val model: LabeledTextCellModel
) : BaseCellViewModel(model)