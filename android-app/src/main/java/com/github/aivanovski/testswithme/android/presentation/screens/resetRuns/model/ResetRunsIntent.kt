package com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model

sealed interface ResetRunsIntent {

    data object Initialize : ResetRunsIntent

    data object OnResetButtonClick : ResetRunsIntent

    data class OnVersionSelected(
        val versionName: String
    ) : ResetRunsIntent
}