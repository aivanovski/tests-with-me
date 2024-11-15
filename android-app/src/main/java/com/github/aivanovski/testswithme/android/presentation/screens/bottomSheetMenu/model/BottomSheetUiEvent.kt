package com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model

sealed interface BottomSheetUiEvent {
    data class OnClick(val index: Int) : BottomSheetUiEvent
}