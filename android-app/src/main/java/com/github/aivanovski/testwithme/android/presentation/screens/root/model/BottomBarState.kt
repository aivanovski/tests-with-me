package com.github.aivanovski.testwithme.android.presentation.screens.root.model

import androidx.compose.runtime.Immutable

@Immutable
data class BottomBarState(
    val isVisible: Boolean,
    val selectedIndex: Int,
    val items: List<BottomBarItem>
) {

    companion object {
        val HIDDEN = BottomBarState(
            isVisible = false,
            selectedIndex = 0,
            items = listOf(
                BottomBarItem.PROJECTS,
                BottomBarItem.TEST_RUNS
            )
        )
    }
}