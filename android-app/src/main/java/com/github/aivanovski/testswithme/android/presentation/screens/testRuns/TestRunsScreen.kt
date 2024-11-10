package com.github.aivanovski.testswithme.android.presentation.screens.testRuns

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.entity.ErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.ScreenState
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.IconThreeTextCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.SpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newIconThreeCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newSpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.IconThreeTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model.TestRunsState

@Composable
fun TestRunsScreen(viewModel: TestRunsViewModel) {
    val state by viewModel.state.collectAsState()

    TestRunsScreen(state = state)
}

@Composable
private fun TestRunsScreen(state: TestRunsState) {
    Surface(
        color = AppTheme.theme.colors.secondaryBackground
    ) {
        CellsScreen(
            state = state,
            cellFactory = { cellViewModel ->
                CreateCell(cellViewModel)
            }
        )
    }
}

@Composable
private fun CreateCell(viewModel: BaseCellViewModel) {
    return when (viewModel) {
        is IconThreeTextCellViewModel -> IconThreeTextCell(viewModel)
        is SpaceCellViewModel -> SpaceCell(viewModel)
        else -> throw IllegalStateException()
    }
}

@Composable
@Preview
fun TestRunsScreenDataPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        TestRunsScreen(newDataState())
    }
}

@Composable
@Preview
fun TestRunsScreenErrorPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        TestRunsScreen(newErrorState())
    }
}

@Composable
@Preview
fun TestRunsScreenLoadingPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        TestRunsScreen(newLoadingState())
    }
}

@Composable
@Preview
fun TestRunsScreenEmptyPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        TestRunsScreen(newEmptyState())
    }
}

private fun newDataState(): TestRunsState =
    TestRunsState(
        screenState = null,
        viewModels = listOf(
            newSpaceCellViewModel(ElementMargin),
            newIconThreeCellViewModel(),
            newSpaceCellViewModel(SmallMargin),
            newIconThreeCellViewModel()
        )
    )

@Composable
private fun newErrorState(): TestRunsState =
    TestRunsState(
        screenState = ScreenState.Error(
            message = ErrorMessage(
                stringResource(R.string.error_has_been_occurred),
                Exception()
            )
        )
    )

private fun newLoadingState(): TestRunsState =
    TestRunsState(
        screenState = ScreenState.Loading
    )

@Composable
private fun newEmptyState(): TestRunsState =
    TestRunsState(
        screenState = ScreenState.Empty(
            message = stringResource(R.string.no_tests)
        )
    )