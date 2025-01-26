package com.github.aivanovski.testswithme.android.presentation.screens.textViewer

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.aivanovski.testswithme.android.presentation.core.cells.CreateCoreCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.screens.textViewer.model.TextViewerIntent
import com.github.aivanovski.testswithme.android.presentation.screens.textViewer.model.TextViewerState

@Composable
fun TextViewerScreen(viewModel: TextViewerViewModel) {
    val state by viewModel.state.collectAsState()

    TextViewerScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun TextViewerScreen(
    state: TextViewerState,
    onIntent: (intent: TextViewerIntent) -> Unit
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