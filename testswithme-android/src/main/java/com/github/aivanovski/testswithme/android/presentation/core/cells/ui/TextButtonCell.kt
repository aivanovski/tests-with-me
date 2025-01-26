package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextButtonCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextButtonCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TextButtonCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.OneLineItemHeight
import com.github.aivanovski.testswithme.android.presentation.core.compose.toComposeShape

@Composable
fun TextButtonCell(viewModel: TextButtonCellViewModel) {
    val model = viewModel.model

    val onClick = rememberOnClickedCallback {
        viewModel.sendIntent(TextButtonCellIntent.OnClick(model.id))
    }

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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .defaultMinSize(minHeight = OneLineItemHeight)
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Text(
                text = model.text,
                color = AppTheme.theme.colors.primaryText,
                style = AppTheme.theme.typography.titleMedium
            )
        }
    }
}

@Composable
@Preview
fun TextButtonCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            TextButtonCell(newTextButtonCell())
        }
    }
}

fun newTextButtonCell(shape: CornersShape = CornersShape.ALL) =
    TextButtonCellViewModel(
        model = TextButtonCellModel(
            id = "id",
            text = "VIEW ALL",
            shape = shape
        ),
        intentProvider = PreviewIntentProvider
    )