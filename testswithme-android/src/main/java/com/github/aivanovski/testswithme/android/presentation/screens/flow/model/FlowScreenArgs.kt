package com.github.aivanovski.testswithme.android.presentation.screens.flow.model

import kotlinx.serialization.Serializable

@Serializable
data class FlowScreenArgs(
    val mode: FlowScreenMode,
    val screenTitle: String
)