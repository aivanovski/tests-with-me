package com.github.aivanovski.testswithme.android.presentation.screens.projects.model

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreenState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.ScreenState

@Immutable
data class ProjectsState(
    override val screenState: ScreenState? = null,
    override val viewModels: List<CellViewModel> = emptyList(),
    val isAddButtonVisible: Boolean = false
) : CellsScreenState