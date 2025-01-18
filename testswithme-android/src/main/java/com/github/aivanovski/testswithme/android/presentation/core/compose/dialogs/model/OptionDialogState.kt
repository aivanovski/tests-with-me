package com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model

import androidx.compose.runtime.Immutable

@Immutable
data class OptionDialogState(
    val options: List<String>,
    val actions: List<DialogAction>
)