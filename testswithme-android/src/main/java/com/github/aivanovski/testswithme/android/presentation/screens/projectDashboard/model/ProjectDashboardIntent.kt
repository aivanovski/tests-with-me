package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model

import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction

sealed interface ProjectDashboardIntent {

    data object Initialize : ProjectDashboardIntent

    data object OnAddButtonClick : ProjectDashboardIntent

    data object ReloadData : ProjectDashboardIntent

    data object OnDismissOptionDialog : ProjectDashboardIntent

    data object OnDismissMessageDialog : ProjectDashboardIntent

    data class OnOptionDialogClick(
        val action: DialogAction
    ) : ProjectDashboardIntent

    data class OnMessageDialogClick(
        val action: DialogAction
    ) : ProjectDashboardIntent

    data class OnVersionClick(
        val versionName: String
    ) : ProjectDashboardIntent
}