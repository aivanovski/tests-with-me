package com.github.aivanovski.testwithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Column
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
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.IconChipItem
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.IconChipRowCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.toComposeColor
import com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel.IconChipRowCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testwithme.android.presentation.core.compose.IconChip
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.toComposeShape

@Composable
fun IconChipRowCell(viewModel: IconChipRowCellViewModel) {
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

                IconChip(
                    icon = chip.icon,
                    iconTint = chip.iconTint.toComposeColor(),
                    text = chip.text,
                    textColor = chip.textColor,
                    cardColor = chip.chipColor,
                )
            }
        }
    }
}

@Composable
@Preview
fun IconChipRowCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            IconChipRowCell(newIconChipCell(shape = CornersShape.TOP))
        }
    }
}

@Composable
fun newIconChipCell(
    shape: CornersShape = CornersShape.ALL
) = IconChipRowCellViewModel(
    model = IconChipRowCellModel(
        id = "id",
        chips = listOf(
            IconChipItem(
                icon = AppIcons.CheckCircle,
                iconTint = IconTint.GREEN,
                text = "128",
                textColor = AppTheme.theme.colors.primaryText,
                chipColor = AppTheme.theme.colors.cardOnPrimaryBackground
            ),
            IconChipItem(
                icon = AppIcons.ErrorCircle,
                iconTint = IconTint.RED,
                text = "13",
                textColor = AppTheme.theme.colors.primaryText,
                chipColor = AppTheme.theme.colors.cardOnPrimaryBackground,
            )
        ),
        shape = shape
    )
)