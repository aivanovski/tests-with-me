package com.github.aivanovski.testswithme.android.presentation.screens.flow.model

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.entity.ErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreenState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogState
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.OptionDialogState

@Immutable
data class FlowState(
    override val terminalState: TerminalState? = null,
    override val viewModels: List<CellViewModel> = emptyList(),
    val optionDialogState: OptionDialogState? = null,
    val errorDialogMessage: ErrorMessage? = null,
    val flowDialogState: MessageDialogState? = null,
    val isUploadButtonVisible: Boolean = false
) : CellsScreenState