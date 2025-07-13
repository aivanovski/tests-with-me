package com.github.aivanovski.testswithme.android.presentation.core.compose.theme

import android.content.Context

interface ThemeProvider {
    val theme: Theme
    fun onThemedContextCreated(context: Context)
}