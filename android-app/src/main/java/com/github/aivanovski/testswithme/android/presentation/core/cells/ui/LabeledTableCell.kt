package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTableCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.LabeledTableCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineSmallItemHeight
import com.github.aivanovski.testswithme.android.presentation.core.compose.toComposeShape

@Composable
fun LabeledTableCell(viewModel: LabeledTableCellViewModel) {
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
        val columnCount = model.labels.size

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ElementMargin
                )
                .defaultMinSize(minHeight = TwoLineSmallItemHeight)
        ) {
            for (columnIndex in 0 until columnCount) {
                val labelText = model.labels[columnIndex]
                val text = model.values[columnIndex]

                val weight = 1f / columnCount

                Column(
                    modifier = Modifier
                        .weight(weight = weight)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = labelText,
                        color = AppTheme.theme.colors.secondaryText,
                        style = AppTheme.theme.typography.bodySmall
                    )

                    Text(
                        text = text,
                        color = AppTheme.theme.colors.primaryText,
                        style = AppTheme.theme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun LabeledTableCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            LabeledTableCell(newOneColumnTableCell(shape = CornersShape.TOP))
            LabeledTableCell(newTwoColumnsTableCell(shape = CornersShape.NONE))
            LabeledTableCell(newThreeColumnsTableCell(shape = CornersShape.BOTTOM))
        }
    }
}

fun newTableCellViewModel(
    labels: List<String>,
    values: List<String>,
    shape: CornersShape = CornersShape.ALL
) = LabeledTableCellViewModel(
    model = LabeledTableCellModel(
        id = "id",
        labels = labels,
        values = values,
        shape = shape
    )
)

fun newOneColumnTableCell(shape: CornersShape = CornersShape.ALL) =
    LabeledTableCellViewModel(
        model = LabeledTableCellModel(
            id = "id",
            labels = listOf("Column 1"),
            values = listOf("Value 1"),
            shape = shape
        )
    )

fun newTwoColumnsTableCell(shape: CornersShape = CornersShape.ALL) =
    LabeledTableCellViewModel(
        model = LabeledTableCellModel(
            id = "id",
            labels = listOf("Column 1", "Column 2"),
            values = listOf("Value 1", "Value 2"),
            shape = shape
        )
    )

fun newThreeColumnsTableCell(shape: CornersShape = CornersShape.ALL) =
    LabeledTableCellViewModel(
        model = LabeledTableCellModel(
            id = "id",
            labels = listOf("Column 1", "Column 2", "Column 3"),
            values = listOf("Value 1", "Value 2", "Value 3"),
            shape = shape
        )
    )