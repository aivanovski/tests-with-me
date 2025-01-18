package com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model

import kotlinx.serialization.Serializable

@Serializable
data class ResetRunsScreenArgs(
    val projectUid: String
)