package com.github.aivanovski.testwithme.android.presentation.screens.groups.model

import androidx.compose.runtime.Immutable
import com.github.aivanovski.testwithme.android.entity.ErrorMessage
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel

@Immutable
sealed interface GroupsState {

    @Immutable
    object NotInitialized : GroupsState

    @Immutable
    object Loading : GroupsState

    @Immutable
    data class Data(
        val viewModels: List<BaseCellViewModel>
    ) : GroupsState

    @Immutable
    data class Error(
        val message: ErrorMessage
    ) : GroupsState
}