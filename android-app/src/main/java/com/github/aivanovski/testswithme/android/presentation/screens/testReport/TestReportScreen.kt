package com.github.aivanovski.testswithme.android.presentation.screens.testReport

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.aivanovski.testswithme.android.presentation.core.cells.CreateCoreCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.screens.testReport.model.TestReportIntent
import com.github.aivanovski.testswithme.android.presentation.screens.testReport.model.TestReportState

@Composable
fun TestReportScreen(viewModel: TestReportViewModel) {
    val state by viewModel.state.collectAsState()

    TestReportScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun TestReportScreen(
    state: TestReportState,
    onIntent: (intent: TestReportIntent) -> Unit
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