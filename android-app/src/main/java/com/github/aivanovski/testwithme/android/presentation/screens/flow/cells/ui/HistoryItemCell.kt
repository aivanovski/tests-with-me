package com.github.aivanovski.testwithme.android.presentation.screens.flow.cells.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.toComposeColor
import com.github.aivanovski.testwithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testwithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.CardCornerSize
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.HalfMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.TwoLineSmallItemHeight
import com.github.aivanovski.testwithme.android.presentation.screens.flow.cells.model.HistoryItemCellIntent
import com.github.aivanovski.testwithme.android.presentation.screens.flow.cells.model.HistoryItemCellModel
import com.github.aivanovski.testwithme.android.presentation.screens.flow.cells.viewModel.HistoryItemCellViewModel

@Composable
fun HistoryItemCell(viewModel: HistoryItemCellViewModel) {
    val model = viewModel.model

    val onItemClick = rememberOnClickedCallback {
        viewModel.sendIntent(HistoryItemCellIntent.OnItemClick(model.id))
    }

    Card(
        shape = RoundedCornerShape(size = CardCornerSize),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.theme.colors.cardOnSecondaryBackground
        ),
        modifier = Modifier
            .padding(horizontal = ElementMargin)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onItemClick)
                .padding(horizontal = ElementMargin)
                .height(height = TwoLineSmallItemHeight) // TODO: dimen
        ) {
            Icon(
                imageVector = model.icon,
                tint = model.iconTint.toComposeColor(),
                contentDescription = null
            )

            Column(
                modifier = Modifier
                    .padding(start = HalfMargin)
            ) {
                Text(
                    text = model.title,
                    color = AppTheme.theme.colors.primaryText,
                    style = AppTheme.theme.typography.bodyLarge
                )

                Text(
                    text = model.description,
                    color = AppTheme.theme.colors.secondaryText,
                    style = AppTheme.theme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
@Preview
fun HistoryItemCellLightPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            HistoryItemCell(newSuccessHistoryItemCellViewModel())
            Spacer(modifier = Modifier.height(8.dp))
            HistoryItemCell(newFailedHistoryItemCellViewModel())
        }
    }
}

fun newSuccessHistoryItemCellViewModel(): HistoryItemCellViewModel {
    return HistoryItemCellViewModel(
        model = HistoryItemCellModel(
            id = "id",
            icon = AppIcons.CheckCircle,
            iconTint = IconTint.GREEN,
            title = "5 ming ago",
            description = "by Admin"
        ),
        intentProvider = PreviewIntentProvider
    )
}

fun newFailedHistoryItemCellViewModel(): HistoryItemCellViewModel {
    return HistoryItemCellViewModel(
        model = HistoryItemCellModel(
            id = "id",
            icon = AppIcons.ErrorCircle,
            iconTint = IconTint.RED,
            title = "5 ming ago",
            description = "by Admin"
        ),
        intentProvider = PreviewIntentProvider
    )
}