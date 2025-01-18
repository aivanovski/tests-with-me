package com.github.aivanovski.testswithme.cli.presentation.main.model

sealed interface TestState {
    data object Awaiting : TestState
    data object Passed : TestState
    data object Failed : TestState
    data object Sending : TestState
    data object Running : TestState
    data class Error(val message: String) : TestState
}