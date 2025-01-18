package com.github.aivanovski.testswithme.android.presentation.screens.resetRuns

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
import com.github.aivanovski.testswithme.android.presentation.core.compose.ErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.compose.ProgressIndicator
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model.ResetRunsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model.ResetRunsState

@Composable
fun ResetRunsScreen(viewModel: ResetRunsViewModel) {
    val state by viewModel.state.collectAsState()

    ResetRunsScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun ResetRunsScreen(
    state: ResetRunsState,
    onIntent: (intent: ResetRunsIntent) -> Unit
) {
    when (state.terminalState) {
        TerminalState.Loading -> {
            ProgressIndicator()
        }

        is TerminalState.Error -> {
            CenteredBox {
                ErrorMessage(message = state.terminalState.message)
            }
        }

        else -> {
            DataContent(
                versions = state.versions,
                selectedVersion = state.selectedVersion,
                onIntent = onIntent
            )
        }
    }
}

@Composable
private fun DataContent(
    versions: List<String>,
    selectedVersion: String,
    onIntent: (intent: ResetRunsIntent) -> Unit
) {
    val onResetClick = rememberOnClickedCallback {
        onIntent.invoke(ResetRunsIntent.OnResetButtonClick)
    }
    val onVersionSelected = rememberCallback { version: String ->
        onIntent.invoke(ResetRunsIntent.OnVersionSelected(version))
    }

    Column(
        modifier = Modifier
            .padding(
                start = ElementMargin,
                end = ElementMargin,
                top = GroupMargin
            )
            .fillMaxWidth()
    ) {
        AppDropdownMenu(
            label = stringResource(R.string.application_version),
            options = versions,
            selectedOption = selectedVersion,
            onOptionSelected = onVersionSelected
        )

        Button(
            onClick = onResetClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.theme.colors.primaryButton
            ),
            modifier = Modifier
                .padding(
                    top = ElementMargin
                )
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.reset)
            )
        }
    }
}

@Composable
@Preview
fun ResetRunsScreen_DataPreview() {
    ThemedScreenPreview(
        theme = LightTheme
    ) {
        ResetRunsScreen(
            state = newDataState(),
            onIntent = {}
        )
    }
}

fun newDataState() =
    ResetRunsState(
        selectedVersion = "1.1.0",
        versions = listOf(
            "1.1.0"
        )
    )