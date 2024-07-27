package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.LabeledTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineSmallItemHeight
import com.github.aivanovski.testswithme.android.presentation.core.compose.toComposeShape

@Composable
fun LabeledTextCell(viewModel: LabeledTextCellViewModel) {
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
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = TwoLineSmallItemHeight)
                .padding(
                    horizontal = ElementMargin,
                    vertical = SmallMargin
                )
        ) {
            Text(
                text = model.label,
                color = AppTheme.theme.colors.secondaryText,
                style = AppTheme.theme.typography.bodySmall
            )

            Text(
                text = model.text,
                color = AppTheme.theme.colors.primaryText,
                style = AppTheme.theme.typography.bodyLarge
            )
        }
    }
}

@Composable
@Preview
fun LabeledTextCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            LabeledTextCell(newLabeledTextCell(shape = CornersShape.TOP))
            LabeledTextCell(newLabeledTextCellWithLongText(shape = CornersShape.NONE))
            LabeledTextCell(newLabeledTextCell(shape = CornersShape.BOTTOM))
        }
    }
}

fun newLabeledTextCell(shape: CornersShape = CornersShape.ALL): LabeledTextCellViewModel {
    return LabeledTextCellViewModel(
        model = LabeledTextCellModel(
            id = "id",
            label = "Label",
            text = "Text",
            shape = shape
        )
    )
}

@Composable
fun newLabeledTextCellWithLongText(
    shape: CornersShape = CornersShape.ALL
): LabeledTextCellViewModel {
    return LabeledTextCellViewModel(
        model = LabeledTextCellModel(
            id = "id",
            label = "Label for long text",
            text = stringResource(R.string.long_dummy_text),
            shape = shape
        )
    )
}