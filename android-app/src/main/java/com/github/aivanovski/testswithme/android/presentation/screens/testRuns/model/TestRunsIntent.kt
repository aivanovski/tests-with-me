package com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model

sealed interface TestRunsIntent {
    data object Initialize : TestRunsIntent
    data object ReloadData : TestRunsIntent
}