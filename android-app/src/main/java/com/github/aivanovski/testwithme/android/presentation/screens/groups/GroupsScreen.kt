package com.github.aivanovski.testwithme.android.presentation.screens.groups

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testwithme.android.presentation.core.cells.ui.newSpaceCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.compose.CenteredBox
import com.github.aivanovski.testwithme.android.presentation.core.compose.ErrorMessage
import com.github.aivanovski.testwithme.android.presentation.core.compose.ProgressIndicator
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.dialogs.newErrorMessage
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.GroupsUiCellFactory
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.ui.newFlowCellViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.ui.newGroupCellViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.groups.model.GroupsIntent
import com.github.aivanovski.testwithme.android.presentation.screens.groups.model.GroupsState

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
    val cellFactory = GroupsUiCellFactory()

    Surface(
        color = AppTheme.theme.colors.secondaryBackground
    ) {
        when (state) {
            GroupsState.NotInitialized -> {}

            GroupsState.Loading -> {
                ProgressIndicator()
            }

            is GroupsState.Error -> {
                CenteredBox {
                    ErrorMessage(
                        message = state.message
                    )
                }
            }

            is GroupsState.Data -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(state.viewModels) { viewModel ->
                        cellFactory.createCell(viewModel)
                    }
                }
            }
        }
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

private fun newDataState(): GroupsState =
    GroupsState.Data(
        viewModels = listOf(
            newGroupCellViewModel(),
            newSpaceCellViewModel(height = SmallMargin),
            newFlowCellViewModel()
        )
    )

@Composable
private fun newErrorState(): GroupsState {
    return GroupsState.Error(
        message = newErrorMessage()
    )
}

