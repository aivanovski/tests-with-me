package com.github.aivanovski.testswithme.android.presentation.screens.testRun.model

sealed interface TestRunIntent {

    data object Initialize : TestRunIntent

    data object OnFabClick : TestRunIntent

    data object ReloadData : TestRunIntent
}