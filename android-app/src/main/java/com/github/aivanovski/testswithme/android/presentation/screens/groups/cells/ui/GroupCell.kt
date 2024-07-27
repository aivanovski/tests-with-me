package com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.toComposeColor
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.compose.TextChip
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.CardCornerSize
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.HalfMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineMediumItemHeight
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.GroupCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.GroupCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.GroupCellViewModel

@Composable
fun GroupCell(viewModel: GroupCellViewModel) {
    val model = viewModel.model

    val onClick = rememberOnClickedCallback {
        viewModel.sendIntent(GroupCellIntent.OnClick(model.id))
    }

    val onDetailsClick = rememberOnClickedCallback {
        viewModel.sendIntent(GroupCellIntent.OnDetailsClick(model.id))
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
                modifier = Modifier
                    .padding(horizontal = HalfMargin)
                    .weight(weight = 1f)
            ) {
                Text(
                    text = model.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = AppTheme.theme.colors.primaryText,
                    style = AppTheme.theme.typography.bodyLarge
                )

                Row {
                    for ((index, chip) in model.chips.withIndex()) {
                        if (index > 0) {
                            Spacer(Modifier.width(SmallMargin))
                        }

                        TextChip(
                            text = chip,
                            textSize = TextSize.MEDIUM
                        )
                    }
                }
            }

            Button(
                onClick = onDetailsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.theme.colors.primaryButton
                )
            ) {
                Text(
                    text = stringResource(R.string.details)
                )
            }
        }
    }
}

@Composable
@Preview
fun GroupCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        GroupCell(newGroupCellViewModel())
    }
}

fun newGroupCellViewModel(): GroupCellViewModel {
    return GroupCellViewModel(
        model = GroupCellModel(
            id = "id",
            icon = AppIcons.Folder,
            iconTint = IconTint.PRIMARY_ICON,
            title = "Unlock screen",
            chips = listOf(
                "18 tests",
                "13 executions"
            )
        ),
        intentProvider = PreviewIntentProvider
    )
}