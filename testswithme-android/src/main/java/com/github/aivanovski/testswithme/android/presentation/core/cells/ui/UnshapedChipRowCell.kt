package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextChipItem
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.UnshapedChipRowCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.UnshapedChipRowCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.UnshapedChipRowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.compose.TextChip
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin

@Composable
fun UnshapedChipRowCell(viewModel: UnshapedChipRowCellViewModel) {
    val model = viewModel.model

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
                    viewModel.sendIntent(UnshapedChipRowCellIntent.OnClick(chipIndex = chipIndex))
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

@Composable
@Preview
fun UnshapedChipRowCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        UnshapedChipRowCell(newUnshapedChipRowCell())
    }
}

@Composable
fun newUnshapedChipRowCell(items: List<String> = listOf("admin", "user")) =
    UnshapedChipRowCellViewModel(
        model = UnshapedChipRowCellModel(
            id = "id",
            chips = items.mapIndexed { index, item ->
                TextChipItem(
                    text = item,
                    textColor = AppTheme.theme.colors.primaryText,
                    textSize = TextSize.TITLE,
                    isClickable = false,
                    isSelected = false
                )
            }
        ),
        intentProvider = PreviewIntentProvider
    )