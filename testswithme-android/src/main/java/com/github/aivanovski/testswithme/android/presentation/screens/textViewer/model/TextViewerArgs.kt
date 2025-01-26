package com.github.aivanovski.testswithme.android.presentation.screens.textViewer.model

import kotlinx.serialization.Serializable

@Serializable
data class TextViewerArgs(
    val screenTitle: String,
    val content: String
)