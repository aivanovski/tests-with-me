package com.github.aivanovski.testwithme.android.presentation.screens.projects.model

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.screen.CellsScreenState
import com.github.aivanovski.testwithme.android.presentation.core.cells.screen.TerminalState

@Immutable
data class ProjectsState (
    override val terminalState: TerminalState? = null,
    override val viewModels: List<BaseCellViewModel> = emptyList()
) : CellsScreenState