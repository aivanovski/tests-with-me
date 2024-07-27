package com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.HalfMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.models.TwoTextCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.viewModel.TwoTextCellViewModel

@Composable
fun TwoTextCell(viewModel: TwoTextCellViewModel) {
    val model = viewModel.model

    Column(
        modifier = Modifier
            .padding(
                horizontal = ElementMargin,
                vertical = HalfMargin
            )
            .fillMaxWidth()
    ) {
        Text(
            text = model.title,
            color = AppTheme.theme.colors.secondaryText,
            style = AppTheme.theme.typography.bodyMedium
        )

        Text(
            text = model.description,
            color = AppTheme.theme.colors.primaryText,
            style = AppTheme.theme.typography.bodyLarge
        )
    }
}

@Composable
@Preview
fun TwoTextCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        TwoTextCell(newTwoTextCellViewModel())
    }
}

fun newTwoTextCellViewModel(): TwoTextCellViewModel {
    return TwoTextCellViewModel(
        model = TwoTextCellModel(
            id = "id",
            title = "Title",
            description = "Description"
        )
    )
}