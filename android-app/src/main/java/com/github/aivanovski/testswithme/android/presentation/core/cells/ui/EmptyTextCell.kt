package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.EmptyTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.EmptyTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineMediumItemHeight

@Composable
fun EmptyTextCell(viewModel: EmptyTextCellViewModel) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = TwoLineMediumItemHeight)
    ) {
        Text(
            text = viewModel.model.message,
            color = AppTheme.theme.colors.primaryText,
            style = AppTheme.theme.typography.titleLarge
        )
    }
}

@Composable
@Preview
fun EmptyHistoryCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        EmptyTextCell(newEmptyTextCellViewModel())
    }
}

@Composable
fun newEmptyTextCellViewModel() =
    EmptyTextCellViewModel(
        model = EmptyTextCellModel(
            id = "id",
            message = stringResource(R.string.no_runs)
        )
    )