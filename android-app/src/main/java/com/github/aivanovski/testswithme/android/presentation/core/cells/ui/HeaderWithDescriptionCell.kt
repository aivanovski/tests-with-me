package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderWithDescriptionCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.HeaderWithDescriptionCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.OneLineItemHeight
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.QuarterMargin

@Composable
fun HeaderWithDescriptionCell(viewModel: HeaderWithDescriptionCellViewModel) {
    val model = viewModel.model

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = ElementMargin,
                vertical = QuarterMargin
            )
            .defaultMinSize(minHeight = OneLineItemHeight) // TODO: dimen
    ) {
        Text(
            text = model.title,
            color = AppTheme.theme.colors.primaryText,
            style = AppTheme.theme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
        )

        Text(
            text = model.description,
            color = AppTheme.theme.colors.secondaryText,
            style = AppTheme.theme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
@Preview
fun HeaderWithDescriptionCellPreview() {
    ThemedPreview(theme = LightTheme) {
        Column {
            HeaderWithDescriptionCell(newHeaderWithDescriptionCell())
            Spacer(modifier = Modifier.height(ElementMargin))
            HeaderWithDescriptionCell(
                newHeaderWithDescriptionCell(
                    description = stringResource(R.string.long_dummy_text)
                )
            )
        }
    }
}

fun newHeaderWithDescriptionCell(
    title: String = "Header",
    description: String = "Description"
) = HeaderWithDescriptionCellViewModel(
    model = HeaderWithDescriptionCellModel(
        id = "id",
        title = title,
        description = description
    )
)