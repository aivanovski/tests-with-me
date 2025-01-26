package com.github.aivanovski.testswithme.android.presentation.core

import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.Theme

@Deprecated("Should be removed")
interface ThemeProvider {
    fun getCurrentTheme(): Theme
}