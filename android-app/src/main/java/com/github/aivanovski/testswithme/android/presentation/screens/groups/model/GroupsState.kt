package com.github.aivanovski.testswithme.android.presentation.screens.groups.model

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreenState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.ScreenState
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.OptionDialogState

@Immutable
data class GroupsState(
    override val screenState: ScreenState? = null,
    override val viewModels: List<BaseCellViewModel> = emptyList(),
    val optionDialogState: OptionDialogState? = null
) : CellsScreenState