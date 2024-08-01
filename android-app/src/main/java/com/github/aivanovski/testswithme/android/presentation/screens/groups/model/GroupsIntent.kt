package com.github.aivanovski.testswithme.android.presentation.screens.groups.model

import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction

sealed interface GroupsIntent {

    data object Initialize : GroupsIntent

    data object ReloadData : GroupsIntent

    data object OnAddButtonClick : GroupsIntent

    data object OnDismissOptionDialog : GroupsIntent

    data class OnOptionDialogClick(
        val action: DialogAction
    ) : GroupsIntent
}