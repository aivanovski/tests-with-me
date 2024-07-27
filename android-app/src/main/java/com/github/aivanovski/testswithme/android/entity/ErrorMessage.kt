package com.github.aivanovski.testswithme.android.entity

import androidx.compose.runtime.Immutable

@Immutable
data class ErrorMessage(
    val message: String,
    val cause: Exception
)