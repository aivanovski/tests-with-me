package com.github.aivanovski.testwithme.android.presentation.screens.flow.model

sealed interface FlowUiEvent {

    object ShowAccessibilityServices : FlowUiEvent

    data class OpenUrl(
        val url: String
    ) : FlowUiEvent
}