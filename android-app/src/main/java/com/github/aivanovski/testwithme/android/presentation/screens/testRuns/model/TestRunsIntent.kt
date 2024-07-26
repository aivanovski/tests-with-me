package com.github.aivanovski.testwithme.android.presentation.screens.testRuns.model

sealed interface TestRunsIntent {
    object Initialize : TestRunsIntent
}