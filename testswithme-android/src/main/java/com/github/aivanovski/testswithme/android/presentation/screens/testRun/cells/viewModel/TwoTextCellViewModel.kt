package com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.models.TwoTextCellModel

@Immutable
class TwoTextCellViewModel(
    override val model: TwoTextCellModel
) : BaseCellViewModel(model)