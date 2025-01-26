package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
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
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TwoLineTextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TwoLineWithChipCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.compose.TextChip
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineSmallItemHeight
import com.github.aivanovski.testswithme.android.presentation.core.compose.toComposeShape

@Composable
fun TwoLineTextWithChipCell(viewModel: TwoLineWithChipCellViewModel) {
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
            Row {
                Text(
                    text = model.title,
                    color = AppTheme.theme.colors.primaryText,
                    style = AppTheme.theme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(weight = 1f)
                )

                if (model.chipText.isNotEmpty()) {
                    TextChip(
                        text = model.chipText,
                        textColor = model.chipTextColor,
                        cardColor = model.chipColor
                    )
                }
            }

            Text(
                text = model.description,
                color = AppTheme.theme.colors.secondaryText,
                style = AppTheme.theme.typography.bodySmall
            )
        }
    }
}

@Composable
@Preview
fun TwoLineTextWithChipCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            TwoLineTextWithChipCell(newPassedTwoLineWithChipCell(shape = CornersShape.TOP))
            TwoLineTextWithChipCell(newFailedTwoLineWithChipCell(shape = CornersShape.NONE))
            TwoLineTextWithChipCell(newTwoLineWithChipCell(shape = CornersShape.BOTTOM))
        }
    }
}

@Composable
fun newTwoLineWithChipCell(shape: CornersShape = CornersShape.ALL): TwoLineWithChipCellViewModel =
    TwoLineWithChipCellViewModel(
        model = TwoLineTextWithChipCellModel(
            id = "id",
            title = "- sendBroadcast:",
            description = "  - name: org.wikipedia/open.action\n  - data: [...]",
            chipText = "",
            chipColor = Color.Transparent,
            chipTextColor = Color.Transparent,
            shape = shape
        ),
        intentProvider = PreviewIntentProvider
    )

@Composable
fun newPassedTwoLineWithChipCell(
    shape: CornersShape = CornersShape.ALL
): TwoLineWithChipCellViewModel =
    TwoLineWithChipCellViewModel(
        model = TwoLineTextWithChipCellModel(
            id = "id",
            title = "- sendBroadcast:",
            description = "  - name: org.wikipedia/open.action\n  - data: [...]",
            chipText = "SUCCESS",
            chipColor = AppTheme.theme.colors.greenCard,
            chipTextColor = AppTheme.theme.colors.testGreen,
            shape = shape
        ),
        intentProvider = PreviewIntentProvider
    )

@Composable
fun newFailedTwoLineWithChipCell(
    shape: CornersShape = CornersShape.ALL
): TwoLineWithChipCellViewModel =
    TwoLineWithChipCellViewModel(
        model = TwoLineTextWithChipCellModel(
            id = "id",
            title = "- sendBroadcast:",
            description = "  - name: org.wikipedia/open.action\n  - data: [...]",
            chipText = "FAILED",
            chipColor = AppTheme.theme.colors.redCard,
            chipTextColor = AppTheme.theme.colors.testRed,
            shape = shape
        ),
        intentProvider = PreviewIntentProvider
    )