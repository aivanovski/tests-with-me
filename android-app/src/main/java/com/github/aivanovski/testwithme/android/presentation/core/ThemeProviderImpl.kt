package com.github.aivanovski.testwithme.android.presentation.core

import android.content.Context
import android.content.res.Configuration
import com.github.aivanovski.testwithme.android.App
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.DarkTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.Theme

class ThemeProviderImpl(
    private val themedContext: Context
) : ThemeProvider {

    init {
        if (themedContext is App) {
            throw IllegalArgumentException()
        }
    }

    override fun getCurrentTheme(): Theme {
        val isNightMode = isNightMode(themedContext)
        return if (isNightMode) {
            DarkTheme
        } else {
            LightTheme
        }
    }

    private fun isNightMode(context: Context): Boolean {
        val mode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }
}