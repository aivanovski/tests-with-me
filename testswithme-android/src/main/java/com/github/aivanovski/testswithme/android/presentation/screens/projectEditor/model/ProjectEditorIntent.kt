package com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model

sealed interface ProjectEditorIntent {

    object Initialize : ProjectEditorIntent

    object OnDoneMenuClick : ProjectEditorIntent

    data class OnPackageNameChanged(
        val packageName: String
    ) : ProjectEditorIntent

    data class OnNameChanged(
        val name: String
    ) : ProjectEditorIntent

    data class OnDescriptionChanged(
        val description: String
    ) : ProjectEditorIntent

    data class OnSiteUrlChanged(
        val siteUrl: String
    ) : ProjectEditorIntent

    data class OnDownloadUrlChanged(
        val downloadUrl: String
    ) : ProjectEditorIntent

    data class OnMessageDialogClick(
        val actionId: Int
    ) : ProjectEditorIntent
}