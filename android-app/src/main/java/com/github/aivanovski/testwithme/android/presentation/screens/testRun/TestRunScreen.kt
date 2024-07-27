package com.github.aivanovski.testwithme.android.presentation.screens.testRun

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testwithme.android.presentation.core.cells.ui.HeaderCell
import com.github.aivanovski.testwithme.android.presentation.core.cells.ui.SpaceCell
import com.github.aivanovski.testwithme.android.presentation.core.cells.ui.newHeaderCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel.HeaderCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.cells.ui.TextCell
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.cells.ui.newLongTextCell
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.cells.ui.newShortTextCell
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.cells.viewModel.TextCellViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.model.TestRunIntent
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.model.TestRunState

@Composable
fun TestRunScreen(viewModel: TestRunViewModel) {
    val state by viewModel.state.collectAsState()

    TestRunScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun TestRunScreen(
    state: TestRunState,
    onIntent: (intent: TestRunIntent) -> Unit
) {
    val onFabClick = rememberOnClickedCallback {
        onIntent.invoke(TestRunIntent.OnFabClick)
    }

    Surface(
        color = AppTheme.theme.colors.secondaryBackground
    ) {
        CellsScreen(
            state = state,
            cellFactory = { cellViewModel ->
                CreateCell(cellViewModel)
            }
        )

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
        ) {
            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier
                    .padding(
                        end = ElementMargin,
                        bottom = ElementMargin
                    )
            ) {
                Icon(
                    imageVector = AppIcons.Upload,
                    tint = AppTheme.theme.colors.primaryText,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun CreateCell(cellViewModel: BaseCellViewModel) {
    when (cellViewModel) {
        is SpaceCellViewModel -> SpaceCell(cellViewModel)
        is TextCellViewModel -> TextCell(cellViewModel)
        is HeaderCellViewModel -> HeaderCell(cellViewModel)
        else -> throw IllegalStateException()
    }
}

@Composable
@Preview
fun TestRunScreenPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        TestRunScreen(
            state = newDataState(),
            onIntent = {}
        )
    }
}

private fun newDataState(): TestRunState =
    TestRunState(
        terminalState = null,
        viewModels = listOf(
            newHeaderCellViewModel(),
            newShortTextCell(),
            newLongTextCell()
        )
    )