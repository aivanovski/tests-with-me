package com.github.aivanovski.testswithme.android.presentation.screens.settings.model

import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction

sealed interface SettingsIntent {

    data object Initialize : SettingsIntent

    data object ReloadData : SettingsIntent

    data object OnDismissOptionDialog : SettingsIntent

    data class OnOptionDialogClick(
        val action: DialogAction
    ) : SettingsIntent
}