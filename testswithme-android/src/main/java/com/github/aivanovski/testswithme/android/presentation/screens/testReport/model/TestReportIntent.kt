package com.github.aivanovski.testswithme.android.presentation.screens.testReport.model

sealed interface TestReportIntent {

    data object Initialize : TestReportIntent
}