package com.github.aivanovski.testwithme.android.presentation.screens.groups.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupsScreenArgs(
    val projectUid: String,
    val groupUid: String?
)