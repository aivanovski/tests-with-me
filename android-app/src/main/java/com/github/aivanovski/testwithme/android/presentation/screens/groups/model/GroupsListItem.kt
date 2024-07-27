package com.github.aivanovski.testwithme.android.presentation.screens.groups.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface GroupsListItem {

    @Immutable
    data class GroupItem(
        val uid: String,
        val name: String
    ) : GroupsListItem

    @Immutable
    data class FlowItem(
        val uid: String,
        val name: String
    ) : GroupsListItem
}