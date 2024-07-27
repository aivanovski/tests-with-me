package com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ShapedSpaceCellModel

@Immutable
class ShapedSpaceCellViewModel(
    override val model: ShapedSpaceCellModel
) : BaseCellViewModel(model)