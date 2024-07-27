package com.github.aivanovski.testswithme.android.presentation.screens.testRun.model

import kotlinx.serialization.Serializable

@Serializable
data class TestRunScreenArgs(
    val jobUid: String,
    val screenTitle: String?
)