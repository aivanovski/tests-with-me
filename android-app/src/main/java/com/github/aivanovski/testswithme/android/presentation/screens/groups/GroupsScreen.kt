package com.github.aivanovski.testswithme.android.presentation.screens.groups

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
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.CreateCoreCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newSpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.OptionDialog
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.newErrorMessage
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
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsIntent.OnDismissOptionDialog
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsIntent.OnOptionDialogClick
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsState

@Composable
fun FlowListScreen(viewModel: GroupsViewModel) {
    val state by viewModel.state.collectAsState()

    FlowListScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun FlowListScreen(
    state: GroupsState,
    onIntent: (intent: GroupsIntent) -> Unit
) {
    val onAddClick = rememberOnClickedCallback {
        onIntent.invoke(GroupsIntent.OnAddButtonClick)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.theme.colors.secondaryBackground)
    ) {
        CellsScreen(
            state = state,
            cellFactory = { cellViewModel -> CreateCell(cellViewModel) }
        )

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

@Composable
private fun CreateCell(cellViewModel: CellViewModel) {
    return when (cellViewModel) {
        is GroupCellViewModel -> GroupCell(cellViewModel)
        is FlowCellViewModel -> FlowCell(cellViewModel)
        else -> CreateCoreCell(cellViewModel)
    }
}

@Preview
@Composable
fun ErrorLightPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        FlowListScreen(
            state = newErrorState(),
            onIntent = {}
        )
    }
}

@Preview
@Composable
fun DataLightPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        FlowListScreen(
            state = newDataState(),
            onIntent = {}
        )
    }
}

private fun newDataState() =
    GroupsState(
        viewModels = listOf(
            newSpaceCell(ElementMargin),
            newGroupCellViewModel(),
            newSpaceCell(SmallMargin),
            newFlowCellViewModel()
        )
    )

@Composable
private fun newErrorState() =
    GroupsState(
        terminalState = TerminalState.Error(newErrorMessage())
    )