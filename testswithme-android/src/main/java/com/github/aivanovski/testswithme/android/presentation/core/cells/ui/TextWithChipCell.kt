package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TextWithChipCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.TextChip
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineSmallItemHeight
import com.github.aivanovski.testswithme.android.presentation.core.compose.toComposeShape

@Composable
fun TextWithChipCell(viewModel: TextWithChipCellViewModel) {
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ElementMargin
                )
                .defaultMinSize(minHeight = TwoLineSmallItemHeight)
        ) {
            Text(
                text = stringResource(R.string.driver),
                color = AppTheme.theme.colors.primaryText,
                style = AppTheme.theme.typography.bodyLarge,
                modifier = Modifier
                    .weight(weight = 1f)
            )

            TextChip(
                text = model.chipText,
                textColor = model.chipTextColor,
                cardColor = model.chipColor
            )
        }
    }
}

@Composable
@Preview
fun TextWithChipCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = ElementMargin)
        ) {
            TextWithChipCell(newTextWithChipGreen())

            Spacer(Modifier.size(ElementMargin))

            TextWithChipCell(newTextWithChipRed())
        }
    }
}

@Composable
fun newTextWithChipGreen(): TextWithChipCellViewModel {
    return TextWithChipCellViewModel(
        model = TextWithChipCellModel(
            id = "id",
            text = "Test Driver",
            chipText = "RUNNING",
            chipColor = AppTheme.theme.colors.greenCard,
            chipTextColor = AppTheme.theme.colors.testGreen,
            shape = CornersShape.ALL
        )
    )
}

@Composable
fun newTextWithChipRed(): TextWithChipCellViewModel {
    return TextWithChipCellViewModel(
        model = TextWithChipCellModel(
            id = "id",
            text = "Test Driver",
            chipText = "STOPPED",
            chipColor = AppTheme.theme.colors.redCard,
            chipTextColor = AppTheme.theme.colors.testRed,
            shape = CornersShape.ALL
        )
    )
}