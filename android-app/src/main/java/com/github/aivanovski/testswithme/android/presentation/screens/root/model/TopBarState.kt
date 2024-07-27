package com.github.aivanovski.testswithme.android.presentation.screens.root.model

import androidx.compose.runtime.Immutable

@Immutable
data class TopBarState(
    val title: String,
    val isBackVisible: Boolean = false
)