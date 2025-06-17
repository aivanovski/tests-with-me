package com.github.aivanovski.testswithme.android.presentation.screens.sandbox.model

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel

@Immutable
sealed interface SandboxState {

    @Immutable
    data object Loading : SandboxState

    @Immutable
    data class Data(
        val viewModels: List<CellViewModel> = emptyList()
    ) : SandboxState
}