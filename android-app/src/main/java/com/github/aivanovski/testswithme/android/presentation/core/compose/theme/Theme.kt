package com.github.aivanovski.testswithme.android.presentation.core.compose.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Immutable
data class Theme(
    val colors: AppColors,
    val materialColors: ColorScheme,
    val typography: Typography
)

val LightTheme = Theme(
    colors = LightAppColors,
    materialColors = lightColorScheme(
        background = LightAppColors.background,
        primaryContainer = LightAppColors.cardOnPrimaryBackground,
        primary = LightAppColors.primary,
        secondary = LightAppColors.secondary,
        tertiary = LightAppColors.tertiary
    ),
    typography = AppTypography
)

val DarkTheme = Theme(
    colors = DarkAppColors,
    materialColors = darkColorScheme(
        background = DarkAppColors.background,
        primaryContainer = DarkAppColors.cardOnPrimaryBackground,
        primary = DarkAppColors.primary,
        secondary = DarkAppColors.secondary,
        tertiary = DarkAppColors.tertiary
    ),
    typography = AppTypography
)

val LocalExtendedColors = staticCompositionLocalOf {
    LightTheme
}

@Composable
fun AppTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            if (view.context is Activity) {
                val window = (view.context as Activity).window
                window.statusBarColor = theme.materialColors.background.toArgb()
                // Make text on status bar visible
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            }
        }
    }

    CompositionLocalProvider(LocalExtendedColors provides theme) {
        MaterialTheme(
            colorScheme = theme.materialColors,
            typography = theme.typography,
            content = content
        )
    }
}

object AppTheme {
    val theme: Theme
        @Composable
        get() = LocalExtendedColors.current
}