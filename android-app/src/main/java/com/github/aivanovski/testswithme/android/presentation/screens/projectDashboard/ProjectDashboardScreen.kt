package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.CreateCoreCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newHeaderWithIconCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newShapedSpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newSpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newTableCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newTextChipRowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newTitleCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.OptionDialog
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
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
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardIntent
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardIntent.OnDismissOptionDialog
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardIntent.OnOptionDialogClick
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardState

@Composable
fun ProjectDashboardScreen(viewModel: ProjectDashboardViewModel) {
    val state by viewModel.state.collectAsState()

    ProjectDashboardScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun ProjectDashboardScreen(
    state: ProjectDashboardState,
    onIntent: (intent: ProjectDashboardIntent) -> Unit
) {
    val onAddClick = rememberOnClickedCallback {
        onIntent.invoke(ProjectDashboardIntent.OnAddButtonClick)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.theme.colors.secondaryBackground)
    ) {
        CellsScreen(
            state = state,
            cellFactory = { cellViewModel ->
                CreateCell(cellViewModel)
            }
        )

        if (state.terminalState is TerminalState.Empty) {
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = ElementMargin,
                        bottom = ElementMargin
                    )
            ) {
                Icon(
                    imageVector = AppIcons.Add,
                    tint = AppTheme.theme.colors.primaryText,
                    contentDescription = null
                )
            }
        }

        if (state.optionDialogState != null) {
            val onDismiss = rememberOnClickedCallback {
                onIntent.invoke(OnDismissOptionDialog)
            }

            val onClick = rememberCallback { action: DialogAction ->
                onIntent.invoke(OnOptionDialogClick(action))
            }

            OptionDialog(
                state = state.optionDialogState,
                onDismiss = onDismiss,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun CreateCell(cellViewModel: CellViewModel) {
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
            state = newDataState(),
            onIntent = {}
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
            state = newEmptyState(),
            onIntent = {}
        )
    }
}

@Composable
private fun newEmptyState() =
    ProjectDashboardState(
        terminalState = TerminalState.Empty(
            message = stringResource(R.string.empty_project_message)
        )
    )

@Composable
private fun newDataState() =
    ProjectDashboardState(
        terminalState = null,
        viewModels = listOf(
            newSpaceCell(height = ElementMargin),
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
            newSpaceCell(SmallMargin),

            newHeaderWithIconCellViewModel(
                title = "Remained Tests",
                iconText = "All"
            ),
            newFlowCellViewModel(),
            newSpaceCell(SmallMargin),
            newFlowCellViewModel(),
            newSpaceCell(SmallMargin),

            newHeaderWithIconCellViewModel(
                title = "Tests",
                iconText = "View"
            ),
            newGroupCellViewModel(),
            newSpaceCell(SmallMargin),
            newGroupCellViewModel(),
            newSpaceCell(SmallMargin),
            newFlowCellViewModel()
        )
    )