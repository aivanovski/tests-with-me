package com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons

enum class BottomSheetIcon(
    val value: ImageVector
) {
    LOGIN(AppIcons.Login),
    LOGOUT(AppIcons.Logout),
    SETTINGS(AppIcons.Settings)
}