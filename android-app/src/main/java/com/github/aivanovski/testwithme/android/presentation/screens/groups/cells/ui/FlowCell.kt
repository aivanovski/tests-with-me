package com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.toComposeColor
import com.github.aivanovski.testwithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testwithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.compose.TextChip
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.CardCornerSize
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.HalfMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.TwoLineMediumItemHeight
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.model.FlowCellIntent
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.model.FlowCellModel
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.viewModel.FlowCellViewModel

@Composable
fun FlowCell(viewModel: FlowCellViewModel) {
    val model = viewModel.model
    val onClick = rememberOnClickedCallback {
        viewModel.sendIntent(FlowCellIntent.OnClick(model.id))
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
                .clickable(onClick = onClick)
                .padding(horizontal = ElementMargin)
                .height(height = TwoLineMediumItemHeight)
        ) {
            Icon(
                imageVector = model.icon,
                tint = model.iconTint.toComposeColor(),
                contentDescription = null
            )

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = SmallMargin)
                    .fillMaxWidth()
            ) {
                Text(
                    text = model.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = AppTheme.theme.colors.primaryText,
                    style = AppTheme.theme.typography.bodyLarge
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (model.chipText.isNotEmpty()) {
                        TextChip(
                            text = model.chipText,
                            textSize = TextSize.MEDIUM
                        )
                    }

                    Text(
                        text = model.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = AppTheme.theme.colors.secondaryText,
                        style = AppTheme.theme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = HalfMargin)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun FlowCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        FlowCell(newFlowCellViewModel())
    }
}

fun newFlowCellViewModel(): FlowCellViewModel {
    return FlowCellViewModel(
        model = FlowCellModel(
            id = "id",
            icon = AppIcons.CheckCircle,
            iconTint = IconTint.GREEN,
            title = "Unlock database",
            description = "5 min ago",
            chipText = "228 executions"
        ),
        intentProvider = PreviewIntentProvider
    )
}