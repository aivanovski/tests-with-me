package com.github.aivanovski.testswithme.android.presentation.screens.testContent.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface TestContentScreenMode {

    @Serializable
    data object FlowContent : TestContentScreenMode

    @Serializable
    data class LocalRun(
        val jobUid: String
    ) : TestContentScreenMode

    @Serializable
    data class RemoteRun(
        val flowRunUid: String
    ) : TestContentScreenMode
}