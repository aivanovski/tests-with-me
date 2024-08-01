package com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model

sealed interface GroupEditorIntent {

    data object Initialize : GroupEditorIntent

    data object OnDoneMenuClick : GroupEditorIntent

    data class OnNameChanged(
        val name: String
    ) : GroupEditorIntent
}