package com.github.aivanovski.testswithme.android.presentation.core.cells.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.entity.ErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.CenteredBox
import com.github.aivanovski.testswithme.android.presentation.core.compose.EmptyMessage
import com.github.aivanovski.testswithme.android.presentation.core.compose.ErrorMessage as ErrorMessageComposable
import com.github.aivanovski.testswithme.android.presentation.core.compose.ProgressIndicator
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme

typealias CellFactory = @Composable (viewModel: CellViewModel) -> Unit

@Composable
fun CellsScreen(
    state: CellsScreenState,
    cellFactory: CellFactory
) {
    val screenState = state.terminalState

    if (screenState != null) {
        TerminalScreenState(state = screenState)
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(state.viewModels) { viewModel ->
                cellFactory.invoke(viewModel)
            }
        }
    }
}

@Composable
fun TerminalScreenState(state: TerminalState) {
    when (state) {
        TerminalState.Loading -> {
            ProgressIndicator()
        }

        is TerminalState.Empty -> {
            CenteredBox {
                EmptyMessage(message = state.message)
            }
        }

        is TerminalState.Error -> {
            CenteredBox {
                ErrorMessageComposable(message = state.message)
            }
        }
    }
}

@Preview
@Composable
fun ScreenScreenState_EmptyPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        TerminalScreenState(
            state = TerminalState.Empty(
                message = "Empty text"
            )
        )
    }
}

@Preview
@Composable
fun ScreenScreenState_LoadingPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        TerminalScreenState(state = TerminalState.Loading)
    }
}

@Preview
@Composable
fun ScreenScreenState_ErrorPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        TerminalScreenState(
            state = TerminalState.Error(
                message = ErrorMessage(
                    message = stringResource(R.string.long_dummy_text),
                    cause = Exception()
                )
            )
        )
    }
}