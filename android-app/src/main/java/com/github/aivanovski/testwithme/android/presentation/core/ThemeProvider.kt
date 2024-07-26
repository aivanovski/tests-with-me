package com.github.aivanovski.testwithme.android.presentation.core

import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.Theme

interface ThemeProvider {
    fun getCurrentTheme(): Theme
}