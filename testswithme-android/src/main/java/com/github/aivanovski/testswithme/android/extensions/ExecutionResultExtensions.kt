package com.github.aivanovski.testswithme.android.extensions

import androidx.compose.ui.graphics.Color
import com.github.aivanovski.testswithme.android.entity.ExecutionResult
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider

fun ExecutionResult.getChipBackgroundColor(themeProvider: ThemeProvider): Color {
    return when (this) {
        ExecutionResult.SUCCESS -> themeProvider.theme.colors.greenCard
        ExecutionResult.FAILED -> themeProvider.theme.colors.redCard
        ExecutionResult.NONE -> Color.Unspecified
    }
}

fun ExecutionResult.getChipTextColor(themeProvider: ThemeProvider): Color {
    return when (this) {
        ExecutionResult.SUCCESS -> themeProvider.theme.colors.testGreen
        ExecutionResult.FAILED -> themeProvider.theme.colors.testRed
        ExecutionResult.NONE -> Color.Unspecified
    }
}