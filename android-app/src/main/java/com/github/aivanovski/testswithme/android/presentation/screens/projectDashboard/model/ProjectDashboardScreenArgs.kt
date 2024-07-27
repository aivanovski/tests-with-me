package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectDashboardScreenArgs(
    val screenTitle: String,
    val projectUid: String
)