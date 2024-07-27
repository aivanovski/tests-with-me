package com.github.aivanovski.testswithme.android.presentation.screens.uploadTest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppDropdownMenu
import com.github.aivanovski.testswithme.android.presentation.core.compose.CenteredBox
import com.github.aivanovski.testswithme.android.presentation.core.compose.EmptyMessage
import com.github.aivanovski.testswithme.android.presentation.core.compose.ErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.compose.ProgressIndicator
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.MessageDialog
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogIntent
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogState
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestIntent
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestState

@Composable
fun UploadTestScreen(viewModel: UploadTestViewModel) {
    val state by viewModel.state.collectAsState()

    UploadTestScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun UploadTestScreen(
    state: UploadTestState,
    onIntent: (intent: UploadTestIntent) -> Unit
) {
    val onProjectSelected = rememberCallback { project: String ->
        onIntent.invoke(UploadTestIntent.OnProjectSelected(project))
    }
    val onGroupSelected = rememberCallback { group: String ->
        onIntent.invoke(UploadTestIntent.OnGroupSelected(group))
    }

    val onUploadClick = rememberOnClickedCallback {
        onIntent.invoke(UploadTestIntent.OnUploadButtonClick)
    }

    when (state.terminalState) {
        TerminalState.Loading -> {
            ProgressIndicator()
        }

        is TerminalState.Error -> {
            CenteredBox {
                ErrorMessage(message = state.terminalState.message)
            }
        }

        is TerminalState.Empty -> {
            CenteredBox {
                EmptyMessage(message = state.terminalState.message)
            }
        }

        else -> {
            DataContent(
                projects = state.projects,
                selectedProject = state.selectedProject,
                groups = state.groups,
                selectedGroup = state.selectedGroup,
                onProjectSelected = onProjectSelected,
                onGroupSelected = onGroupSelected,
                onUploadClick = onUploadClick
            )

            if (state.dialogState != null) {
                MessageDialogContent(
                    state = state.dialogState,
                    onIntent = onIntent
                )
            }
        }
    }
}

@Composable
private fun DataContent(
    projects: List<String>,
    selectedProject: String,
    groups: List<String>,
    selectedGroup: String,
    onProjectSelected: (project: String) -> Unit,
    onGroupSelected: (group: String) -> Unit,
    onUploadClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AppDropdownMenu(
            label = stringResource(R.string.project),
            options = projects,
            selectedOption = selectedProject,
            onOptionSelected = onProjectSelected,
            modifier = Modifier
                .padding(
                    start = ElementMargin,
                    end = ElementMargin,
                    top = GroupMargin
                )
                .fillMaxWidth()
        )

        AppDropdownMenu(
            label = stringResource(R.string.group),
            options = groups,
            selectedOption = selectedGroup,
            onOptionSelected = onGroupSelected,
            modifier = Modifier
                .padding(
                    start = ElementMargin,
                    end = ElementMargin,
                    top = ElementMargin
                )
                .fillMaxWidth()
        )

        Button(
            onClick = onUploadClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.theme.colors.primaryButton
            ),
            modifier = Modifier
                .padding(
                    start = ElementMargin,
                    end = ElementMargin,
                    top = ElementMargin
                )
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.upload)
            )
        }
    }
}

@Composable
private fun MessageDialogContent(
    state: MessageDialogState,
    onIntent: (intent: UploadTestIntent) -> Unit
) {
    MessageDialog(
        state = state,
        onIntent = { intent ->
            if (intent is MessageDialogIntent.OnActionButtonClick) {
                onIntent.invoke(UploadTestIntent.OnDialogActionClick(intent.actionId))
            }
        }
    )
}

@Composable
@Preview
fun UploadTestScreenPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        UploadTestScreen(
            state = newDataState(),
            onIntent = {}
        )
    }
}

private fun newDataState(): UploadTestState =
    UploadTestState(
        terminalState = null,
        projects = listOf("KeePassVault", "F-Droid"),
        selectedProject = "KeePassVault",
        groups = listOf("Root"),
        selectedGroup = "Root"
    )