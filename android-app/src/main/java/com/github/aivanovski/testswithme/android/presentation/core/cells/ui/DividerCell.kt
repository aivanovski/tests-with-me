package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.DividerCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.DividerCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ElementSpace
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme

@Composable
fun DividerCell(viewModel: DividerCellViewModel) {
    val model = viewModel.model

    Divider(
        color = model.color,
        modifier = Modifier
            .padding(horizontal = model.padding)
    )
}

@Preview
@Composable
fun DividerCellPreview() {
    ThemedPreview(theme = LightTheme) {
        Column {
            ElementSpace()
            DividerCell(newDividerCell())
            ElementSpace()
        }
    }
}

@Composable
fun newDividerCell() =
    DividerCellViewModel(
        model = DividerCellModel(
            padding = 16.dp,
            color = AppTheme.theme.colors.dividerOnPrimary
        )
    )