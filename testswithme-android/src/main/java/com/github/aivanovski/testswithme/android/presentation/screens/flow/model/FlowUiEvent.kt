package com.github.aivanovski.testswithme.android.presentation.screens.flow.model

sealed interface FlowUiEvent {

    data object ShowAccessibilityServices : FlowUiEvent

    data class OpenUrl(
        val url: String
    ) : FlowUiEvent
}