package com.github.aivanovski.testswithme.android.presentation.screens.projects

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
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.CreateCoreCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newSpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.screens.projects.cells.ui.ProjectCell
import com.github.aivanovski.testswithme.android.presentation.screens.projects.cells.ui.newProjectViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projects.cells.viewModel.ProjectCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projects.model.ProjectsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.projects.model.ProjectsState

@Composable
fun ProjectsScreen(viewModel: ProjectsViewModel) {
    val state by viewModel.state.collectAsState()

    ProjectsScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun ProjectsScreen(
    state: ProjectsState,
    onIntent: (intent: ProjectsIntent) -> Unit
) {
    val onAddClick = rememberOnClickedCallback {
        onIntent.invoke(ProjectsIntent.OnAddButtonClick)
    }

    Box(
        modifier = Modifier
            .background(color = AppTheme.theme.colors.secondaryBackground)
            .fillMaxSize()
    ) {
        CellsScreen(
            state = state,
            cellFactory = { viewModel -> CreateCell(viewModel) }
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
}

@Composable
private fun CreateCell(viewModel: BaseCellViewModel) {
    when (viewModel) {
        is ProjectCellViewModel -> ProjectCell(viewModel)
        else -> CreateCoreCell(viewModel)
    }
}

@Composable
@Preview
fun ProjectsScreenPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        ProjectsScreen(
            state = newDataState(),
            onIntent = {}
        )
    }
}

private fun newDataState() =
    ProjectsState(
        viewModels = listOf(
            newSpaceCellViewModel(SmallMargin),
            newProjectViewModel(),
            newSpaceCellViewModel(SmallMargin),
            newProjectViewModel()
        )
    )