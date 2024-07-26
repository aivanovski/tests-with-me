package com.github.aivanovski.testwithme.android.presentation.core.cells.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme

@Immutable
enum class IconTint {
    GREEN,
    RED,
    PRIMARY_ICON
}

@Composable
fun IconTint.toComposeColor(): Color {
    return when (this) {
        IconTint.GREEN -> AppTheme.theme.colors.testGreen
        IconTint.RED -> AppTheme.theme.colors.testRed
        IconTint.PRIMARY_ICON -> AppTheme.theme.colors.primaryIcon
    }
}