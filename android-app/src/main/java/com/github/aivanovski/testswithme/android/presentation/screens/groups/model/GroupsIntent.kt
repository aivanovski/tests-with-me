package com.github.aivanovski.testswithme.android.presentation.screens.groups.model

sealed interface GroupsIntent {

    object Initialize : GroupsIntent

    data class OnFlowClicked(
        val flowUid: String
    ) : GroupsIntent

    data class OnGroupClicked(
        val groupUid: String
    ) : GroupsIntent

    data class OnGroupDetailsClicked(
        val groupUid: String
    ) : GroupsIntent
}