package com.github.aivanovski.testswithme.android.presentation.screens.testContent.model

import kotlinx.serialization.Serializable

@Serializable
data class TestContentArgs(
    val screenTitle: String,
    val flowUid: String,
    val mode: TestContentScreenMode
)