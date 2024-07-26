package com.github.aivanovski.testwithme.android.presentation.screens.root.model

sealed interface RootIntent {

    object NavigateBack : RootIntent

    data class OnMenuClick(
        val menuItem: MenuItem
    ) : RootIntent

    data class SetTopBarState(
        val state: TopBarState
    ) : RootIntent

    data class SetBottomBarState(
        val state: BottomBarState
    ) : RootIntent

    data class SetMenuState(
        val state: MenuState
    ) : RootIntent

    data class OnBottomBarClick(
        val item: BottomBarItem
    ) : RootIntent

    data class ShowToast(
        val message: String
    ) : RootIntent
}