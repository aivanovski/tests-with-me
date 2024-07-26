package com.github.aivanovski.testwithme.android.presentation.core.compose.dialogs.model

import androidx.compose.runtime.Immutable

@Immutable
data class MessageDialogState(
    val title: String?,
    val message: String,
    val isCancellable: Boolean = true,
    val actionButton: MessageDialogButton? = null
)