package com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.compose.ElementSpace
import com.github.aivanovski.testswithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.QuarterMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineMediumItemHeight
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.TwoTextCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.TwoTextCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel.TwoTextCellViewModel

@Composable
fun TwoTextCell(viewModel: TwoTextCellViewModel) {
    val model = viewModel.model

    val onClick = rememberOnClickedCallback {
        viewModel.sendIntent(TwoTextCellIntent.OnClick(model.id))
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = ElementMargin,
                vertical = QuarterMargin
            )
            .defaultMinSize(minHeight = TwoLineMediumItemHeight)
    ) {
        Text(
            text = model.title,
            color = AppTheme.theme.colors.primaryText,
            style = AppTheme.theme.typography.titleMedium
        )

        if (model.description.isNotEmpty()) {
            Text(
                text = model.description,
                color = AppTheme.theme.colors.secondaryText,
                style = AppTheme.theme.typography.bodyMedium
            )
        }
    }
}

@Composable
@Preview
fun TwoTextCellPreview() {
    ThemedPreview(theme = LightTheme) {
        Column {
            TwoTextCell(newTwoTextCell())
            ElementSpace()
            TwoTextCell(newTwoTextCell(description = ""))
        }
    }
}

fun newTwoTextCell(
    title: String = "Title",
    description: String = "Description"
) = TwoTextCellViewModel(
    model = TwoTextCellModel(
        id = "id",
        title = title,
        description = description
    ),
    intentProvider = PreviewIntentProvider
)