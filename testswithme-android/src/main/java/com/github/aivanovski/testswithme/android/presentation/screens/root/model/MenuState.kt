package com.github.aivanovski.testswithme.android.presentation.screens.root.model

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
    }
}