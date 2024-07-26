package com.github.aivanovski.testwithme.android.entity

import androidx.compose.runtime.Immutable

@Immutable
data class ErrorMessage(
    val message: String,
    val cause: Exception
)