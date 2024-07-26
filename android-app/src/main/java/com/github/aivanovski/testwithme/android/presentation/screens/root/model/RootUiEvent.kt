package com.github.aivanovski.testwithme.android.presentation.screens.root.model

sealed interface RootUiEvent {

    data class ShowToast(
        val message: String
    ) : RootUiEvent
}