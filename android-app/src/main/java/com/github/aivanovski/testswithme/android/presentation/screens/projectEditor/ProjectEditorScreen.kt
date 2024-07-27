package com.github.aivanovski.testswithme.android.presentation.screens.projectEditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppTextField
import com.github.aivanovski.testswithme.android.presentation.core.compose.ErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.compose.ProgressIndicator
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.MessageDialog
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogIntent
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogState
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorIntent
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorIntent.OnDescriptionChanged
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorIntent.OnDownloadUrlChanged
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorIntent.OnMessageDialogClick
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorIntent.OnNameChanged
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorIntent.OnPackageNameChanged
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorIntent.OnSiteUrlChanged
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorState

@Composable
fun ProjectEditorScreen(viewModel: ProjectEditorViewModel) {
    val state by viewModel.state.collectAsState()

    ProjectEditorScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun ProjectEditorScreen(
    state: ProjectEditorState,
    onIntent: (intent: ProjectEditorIntent) -> Unit
) {
    val onPackageNameChanged = rememberCallback { newPackageName: String ->
        onIntent.invoke(OnPackageNameChanged(newPackageName))
    }
    val onNameChanged = rememberCallback { newName: String ->
        onIntent.invoke(OnNameChanged(newName))
    }
    val onDescriptionChanged = rememberCallback { newDescription: String ->
        onIntent.invoke(OnDescriptionChanged(newDescription))
    }
    val onUrlChanged = rememberCallback { newSiteUrl: String ->
        onIntent.invoke(OnSiteUrlChanged(newSiteUrl))
    }
    val onDownloadUrlChanged = rememberCallback { newDownloadUrl: String ->
        onIntent.invoke(OnDownloadUrlChanged(newDownloadUrl))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
            state.isLoading -> {
                ProgressIndicator()
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(
                            top = GroupMargin,
                            start = ElementMargin,
                            end = ElementMargin
                        )
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (state.errorMessage != null) {
                        ErrorMessage(message = state.errorMessage)
                    }

                    AppTextField(
                        value = state.packageName,
                        error = state.packageNameError,
                        label = stringResource(R.string.application_package_name),
                        onValueChange = onPackageNameChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    AppTextField(
                        value = state.name,
                        error = state.nameError,
                        label = stringResource(R.string.project_name),
                        onValueChange = onNameChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    AppTextField(
                        value = state.description,
                        label = stringResource(R.string.project_description),
                        onValueChange = onDescriptionChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    AppTextField(
                        value = state.siteUrl,
                        label = stringResource(R.string.site_url),
                        onValueChange = onUrlChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    AppTextField(
                        value = state.downloadUrl,
                        label = stringResource(R.string.download_url),
                        onValueChange = onDownloadUrlChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageDialogContent(
    state: MessageDialogState,
    onIntent: (intent: ProjectEditorIntent) -> Unit
) {
    MessageDialog(
        state = state,
        onIntent = { intent ->
            if (intent is MessageDialogIntent.OnActionButtonClick) {
                onIntent.invoke(OnMessageDialogClick(intent.actionId))
            }
        }
    )
}

@Composable
@Preview
fun ProjectEditorScreenDataPreview() {
    ThemedScreenPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        ProjectEditorScreen(
            state = newDataState(),
            onIntent = {}
        )
    }
}

private fun newDataState() =
    ProjectEditorState(
        packageName = "com.ivanovsky.passnotes",
        name = "KeePassVault",
        description = "KeePass client app for Android",
        siteUrl = "https://github.com/aivanovski/keepassvault",
        downloadUrl = "https://github.com/aivanovski/keepassvault/releases"
    )