package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model

sealed interface ProjectDashboardIntent {
    object Initialize : ProjectDashboardIntent

    data class OnVersionClick(
        val versionName: String
    ) : ProjectDashboardIntent
}