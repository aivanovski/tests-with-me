package com.github.aivanovski.testswithme.android.presentation.screens.groupEditor

import androidx.compose.foundation.layout.Column
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
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.newErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model.GroupEditorIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model.GroupEditorState

@Composable
fun GroupEditorScreen(viewModel: GroupEditorViewModel) {
    val state by viewModel.state.collectAsState()

    GroupEditorScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun GroupEditorScreen(
    state: GroupEditorState,
    onIntent: (intent: GroupEditorIntent) -> Unit
) {
    val onNameChanged = rememberCallback { newName: String ->
        onIntent.invoke(GroupEditorIntent.OnNameChanged(newName))
    }

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
                    value = state.name,
                    error = state.nameError,
                    label = stringResource(R.string.name),
                    onValueChange = onNameChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
@Preview
fun GroupEditorScreenDataPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        GroupEditorScreen(
            state = newDataState(),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun GroupEditorScreenErrorPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        GroupEditorScreen(
            state = newErrorState(),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun GroupEditorScreenLoadingPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        GroupEditorScreen(
            state = newLoadingState(),
            onIntent = {}
        )
    }
}

private fun newLoadingState() =
    GroupEditorState(
        isLoading = true
    )

private fun newDataState() =
    GroupEditorState(
        name = "Name",
        nameError = "Group with this name already exists"
    )

@Composable
private fun newErrorState() =
    GroupEditorState(
        isLoading = false,
        errorMessage = newErrorMessage(),
        name = "Name"
    )