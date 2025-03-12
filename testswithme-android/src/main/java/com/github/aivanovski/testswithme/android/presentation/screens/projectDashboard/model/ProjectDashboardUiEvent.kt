package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model

sealed interface ProjectDashboardUiEvent {

    data class OpenUrl(
        val url: String
    ) : ProjectDashboardUiEvent
}