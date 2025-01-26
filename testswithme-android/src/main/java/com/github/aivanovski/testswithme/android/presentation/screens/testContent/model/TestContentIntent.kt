package com.github.aivanovski.testswithme.android.presentation.screens.testContent.model

sealed interface TestContentIntent {

    data object Initialize : TestContentIntent
}