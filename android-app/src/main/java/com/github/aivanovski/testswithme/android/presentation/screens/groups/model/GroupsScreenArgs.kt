package com.github.aivanovski.testswithme.android.presentation.screens.groups.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupsScreenArgs(
    val projectUid: String,
    val groupUid: String?
)