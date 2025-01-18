package com.github.aivanovski.testswithme.android.presentation.core

import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.Theme

interface ThemeProvider {
    fun getCurrentTheme(): Theme
}