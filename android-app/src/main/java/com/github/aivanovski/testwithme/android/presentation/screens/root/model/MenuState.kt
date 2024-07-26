package com.github.aivanovski.testwithme.android.presentation.screens.root.model

import androidx.compose.runtime.Immutable

@Immutable
data class MenuState(
    val items: List<MenuItem>
) {

    companion object {
        val HIDDEN = MenuState(
            items = emptyList()
        )

        val DONE = MenuState(
            items = listOf(
                MenuItem.DONE
            )
        )

        val LOG_OUT = MenuState(
            items = listOf(
                MenuItem.LOG_OUT
            )
        )
    }
}