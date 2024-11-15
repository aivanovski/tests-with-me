package com.github.aivanovski.testswithme.android.presentation.screens.groups.model

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreenState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.ScreenState
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.OptionDialogState

@Immutable
data class GroupsState(
    override val screenState: ScreenState? = null,
    override val viewModels: List<CellViewModel> = emptyList(),
    val optionDialogState: OptionDialogState? = null
) : CellsScreenState