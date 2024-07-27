package com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface ProjectEditorArgs {

    @Serializable
    object NewProject : ProjectEditorArgs

    @Serializable
    data class EditProject(
        val projectUid: String
    ) : ProjectEditorArgs
}