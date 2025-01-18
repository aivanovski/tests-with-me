package com.github.aivanovski.testswithme.android.presentation.screens.testReport.model

import kotlinx.serialization.Serializable

@Serializable
data class TestReportScreenArgs(
    val flowRunUid: String
)