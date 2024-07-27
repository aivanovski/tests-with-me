package com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model

sealed interface TestRunsIntent {
    object Initialize : TestRunsIntent
}