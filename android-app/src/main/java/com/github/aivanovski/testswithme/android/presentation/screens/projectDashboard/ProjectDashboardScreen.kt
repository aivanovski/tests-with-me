package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.CreateCoreCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newHeaderWithIconCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newShapedSpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newSpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newTableCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newTextChipRowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newTitleCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.ui.FlowCell
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.ui.GroupCell
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.ui.newFlowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.ui.newGroupCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.FlowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.GroupCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.ui.LargeBarCell
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.ui.newLargeBarCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.viewModel.LargeBarCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardState

@Composable
fun ProjectDashboardScreen(viewModel: ProjectDashboardViewModel) {
    val state by viewModel.state.collectAsState()

    ProjectDashboardScreen(
        state = state
    )
}

@Composable
private fun ProjectDashboardScreen(state: ProjectDashboardState) {
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
private fun CreateCell(cellViewModel: BaseCellViewModel) {
    when (cellViewModel) {
        is GroupCellViewModel -> GroupCell(cellViewModel)
        is LargeBarCellViewModel -> LargeBarCell(cellViewModel)
        is FlowCellViewModel -> FlowCell(cellViewModel)
        else -> CreateCoreCell(cellViewModel)
    }
}

@Composable
@Preview
fun ProjectDashboardScreenDataPreview() {
    ThemedScreenPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        ProjectDashboardScreen(
            state = newDataState()
        )
    }
}

@Composable
@Preview
fun ProjectDashboardScreenEmptyPreview() {
    ThemedScreenPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        ProjectDashboardScreen(
            state = newEmptyState()
        )
    }
}

@Composable
private fun newEmptyState() =
    ProjectDashboardState(
        terminalState = TerminalState.Empty(
            message = stringResource(R.string.no_tests_in_project_message)
        )
    )

@Composable
private fun newDataState() =
    ProjectDashboardState(
        terminalState = null,
        viewModels = listOf(
            newSpaceCellViewModel(height = ElementMargin),
            newShapedSpaceCellViewModel(SmallMargin, CornersShape.TOP),
            newTitleCellViewModel(
                title = "Completion",
                shape = CornersShape.NONE
            ),
            newShapedSpaceCellViewModel(SmallMargin, CornersShape.NONE),
            newTextChipRowCellViewModel(shape = CornersShape.NONE), // TODO: show versions
            newLargeBarCellViewModel(),
            newShapedSpaceCellViewModel(ElementMargin, CornersShape.NONE),
            newTableCellViewModel(
                labels = listOf("Completed", "Failed", "Remained"),
                values = listOf("158", "44", "18"),
                shape = CornersShape.NONE
            ),
            newShapedSpaceCellViewModel(SmallMargin, CornersShape.BOTTOM),
            newSpaceCellViewModel(SmallMargin),

            newHeaderWithIconCellViewModel(
                title = "Remained Tests",
                iconText = "All"
            ),
            newFlowCellViewModel(),
            newSpaceCellViewModel(SmallMargin),
            newFlowCellViewModel(),
            newSpaceCellViewModel(SmallMargin),

            newHeaderWithIconCellViewModel(
                title = "Tests",
                iconText = "View"
            ),
            newGroupCellViewModel(),
            newSpaceCellViewModel(SmallMargin),
            newGroupCellViewModel(),
            newSpaceCellViewModel(SmallMargin),
            newFlowCellViewModel()
        )
    )