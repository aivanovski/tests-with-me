package com.github.aivanovski.testwithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TextChipItem
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TextChipRowCellIntent
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TextChipRowCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel.TextChipRowCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.compose.TextChip
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.toComposeShape

@Composable
fun TextChipRowCell(viewModel: TextChipRowCellViewModel) {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = ElementMargin,
                    end = ElementMargin,
                    bottom = SmallMargin
                )
        ) {
            val chipCount = model.chips.size

            for (chipIndex in 0 until chipCount) {
                val chip = model.chips[chipIndex]
                if (chipIndex > 0) {
                    Spacer(modifier = Modifier.width(SmallMargin))
                }

                val cardColor = if (chip.isSelected) {
                    AppTheme.theme.colors.cardOnPrimarySelectedBackground
                } else {
                    AppTheme.theme.colors.cardOnPrimaryBackground
                }

                val onClick = if (chip.isClickable) {
                    rememberOnClickedCallback {
                        viewModel.sendIntent(TextChipRowCellIntent.OnClick(chipIndex = chipIndex))
                    }
                } else {
                    null
                }

                TextChip(
                    text = chip.text,
                    textColor = chip.textColor,
                    textSize = chip.textSize,
                    cardColor = cardColor,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
@Preview
fun TextChipRowCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        TextChipRowCell(newTextChipRowCellViewModel())
    }
}

@Composable
fun newTextChipRowCellViewModel(
    shape: CornersShape = CornersShape.ALL
) = TextChipRowCellViewModel(
    model = TextChipRowCellModel(
        id = "id",
        chips = listOf(
            TextChipItem(
                text = "1.8.0",
                textColor = AppTheme.theme.colors.primaryText,
                textSize = TextSize.LARGE,
                isClickable = false,
                isSelected = true
            ),
            TextChipItem(
                text = "1.7.0",
                textColor = AppTheme.theme.colors.primaryText,
                textSize = TextSize.LARGE,
                isClickable = true,
                isSelected = false
            )
        ),
        shape = shape
    ),
    intentProvider = PreviewIntentProvider
)