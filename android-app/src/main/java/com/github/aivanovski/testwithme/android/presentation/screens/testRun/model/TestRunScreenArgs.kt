package com.github.aivanovski.testwithme.android.presentation.screens.testRun.model

import kotlinx.serialization.Serializable

@Serializable
data class TestRunScreenArgs(
    val jobUid: String,
    val screenTitle: String?
)