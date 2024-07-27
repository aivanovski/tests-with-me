package com.github.aivanovski.testswithme.android.presentation.screens.testRun.model

sealed interface TestRunIntent {

    object Initialize : TestRunIntent

    object OnFabClick : TestRunIntent
}