package com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model

sealed interface UploadTestIntent {

    object Initialize : UploadTestIntent

    object OnUploadButtonClick : UploadTestIntent

    data class OnDialogActionClick(
        val actionId: Int
    ) : UploadTestIntent

    data class OnProjectSelected(
        val projectName: String
    ) : UploadTestIntent

    data class OnGroupSelected(
        val groupName: String
    ) : UploadTestIntent
}