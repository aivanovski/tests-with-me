package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ElementSpace
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.longText
import com.github.aivanovski.testswithme.android.presentation.core.compose.shortText
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.toComposeShape
import com.github.aivanovski.testswithme.android.presentation.core.compose.toTextStyle

@Composable
fun TextCell(viewModel: TextCellViewModel) {
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
            text = model.text,
            color = model.textColor,
            style = model.textSize.toTextStyle(),
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
        Column {
            ElementSpace()
            TextCell(newTextCell(shape = CornersShape.TOP))
            TextCell(newTextCell(shape = CornersShape.BOTTOM))
            ElementSpace()
            TextCell(newTextCell(text = longText()))
            ElementSpace()

            TextCell(
                newTextCell(
                    text = "Error message",
                    textColor = AppTheme.theme.colors.testRed
                )
            )
        }
    }
}

@Composable
fun newTextCell(
    text: String = shortText(),
    textSize: TextSize = TextSize.TITLE,
    textColor: Color = AppTheme.theme.colors.primaryText,
    shape: CornersShape = CornersShape.ALL
) = TextCellViewModel(
    model = TextCellModel(
        id = "id",
        text = text,
        textSize = textSize,
        textColor = textColor,
        shape = shape
    )
)