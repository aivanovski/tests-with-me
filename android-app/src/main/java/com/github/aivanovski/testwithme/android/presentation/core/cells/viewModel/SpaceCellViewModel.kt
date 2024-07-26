package com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.SpaceCellModel

@Immutable
class SpaceCellViewModel(
    override val model: SpaceCellModel
) : BaseCellViewModel(model)