package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TitleCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TitleCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.toComposeShape

@Composable
fun TitleCell(viewModel: TitleCellViewModel) {
    val model = viewModel.model

    Card(
        shape = model.shape.toComposeShape(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.theme.colors.cardOnSecondaryBackground
        ),
        modifier = Modifier
            .padding(
                horizontal = ElementMargin
            )
    ) {
        Text(
            text = viewModel.model.title,
            color = AppTheme.theme.colors.primaryText,
            style = AppTheme.theme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = ElementMargin,
                    end = ElementMargin
                )
        )
    }
}

@Composable
@Preview
fun TitleCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        TitleCell(newTitleCellViewModel())
    }
}

fun newTitleCellViewModel(
    title: String = "Title",
    shape: CornersShape = CornersShape.ALL
) = TitleCellViewModel(
    model = TitleCellModel(
        id = "id",
        title = title,
        shape = shape
    )
)