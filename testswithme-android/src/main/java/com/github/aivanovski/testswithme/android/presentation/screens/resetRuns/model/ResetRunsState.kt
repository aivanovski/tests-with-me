package com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.utils.StringUtils

@Immutable
data class ResetRunsState(
    val terminalState: TerminalState? = null,
    val selectedVersion: String = StringUtils.EMPTY,
    val versions: List<String> = emptyList()
)