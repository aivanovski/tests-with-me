package com.github.aivanovski.testswithme.android.presentation.screens.testContent

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.aivanovski.testswithme.android.presentation.core.cells.CreateCoreCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentIntent
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentState

@Composable
fun TestContentScreen(viewModel: TestContentViewModel) {
    val state by viewModel.state.collectAsState()

    TestContentScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun TestContentScreen(
    state: TestContentState,
    onIntent: (intent: TestContentIntent) -> Unit
) {
    Surface(
        color = AppTheme.theme.colors.secondaryBackground
    ) {
        CellsScreen(
            state = state,
            cellFactory = { cellViewModel ->
                CreateCoreCell(cellViewModel)
            }
        )
    }
}