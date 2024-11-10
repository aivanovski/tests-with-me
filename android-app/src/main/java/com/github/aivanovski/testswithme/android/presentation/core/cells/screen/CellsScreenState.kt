package com.github.aivanovski.testswithme.android.presentation.core.cells.screen

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel

interface CellsScreenState {
    val screenState: ScreenState?
    val viewModels: List<BaseCellViewModel>
}