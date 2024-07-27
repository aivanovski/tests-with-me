package com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model

sealed interface MessageDialogIntent {

    object OnDismiss : MessageDialogIntent

    data class OnActionButtonClick(
        val actionId: Int
    ) : MessageDialogIntent
}