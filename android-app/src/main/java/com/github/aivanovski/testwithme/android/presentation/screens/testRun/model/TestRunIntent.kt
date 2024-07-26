package com.github.aivanovski.testwithme.android.presentation.screens.testRun.model

sealed interface TestRunIntent {

    object Initialize : TestRunIntent

    object OnFabClick : TestRunIntent
}