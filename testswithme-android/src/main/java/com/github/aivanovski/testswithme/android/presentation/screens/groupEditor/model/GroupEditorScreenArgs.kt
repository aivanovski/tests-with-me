package com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface GroupEditorScreenArgs {

    @Serializable
    data class NewGroup(
        val projectUid: String,
        val parentGroupUid: String?
    ) : GroupEditorScreenArgs

    @Serializable
    data class EditGroup(
        val groupUid: String,
        val screenTitle: String
    ) : GroupEditorScreenArgs
}