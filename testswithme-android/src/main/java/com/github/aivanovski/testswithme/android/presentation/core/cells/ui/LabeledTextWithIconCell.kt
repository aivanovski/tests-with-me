package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextWithIconCellIntent.OnIconClick
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextWithIconCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.LabeledTextWithIconCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.MediumIconSize
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.QuarterMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineSmallItemHeight
import com.github.aivanovski.testswithme.android.presentation.core.compose.toComposeShape

@Composable
fun LabeledTextWithIconCell(viewModel: LabeledTextWithIconCellViewModel) {
    val model = viewModel.model

    val onClick = rememberOnClickedCallback {
        viewModel.sendIntent(OnIconClick(model.id))
    }

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
                .defaultMinSize(minHeight = TwoLineSmallItemHeight)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(
                        horizontal = ElementMargin,
                        vertical = SmallMargin
                    )
            ) {
                Text(
                    text = model.label,
                    color = AppTheme.theme.colors.secondaryText,
                    style = AppTheme.theme.typography.bodySmall
                )

                Text(
                    text = model.text,
                    color = AppTheme.theme.colors.primaryText,
                    style = AppTheme.theme.typography.bodyLarge
                )
            }

            if (model.icon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(top = QuarterMargin)
                        .clickable(onClick = onClick)
                        .padding(all = SmallMargin)
                ) {
                    Icon(
                        imageVector = model.icon,
                        contentDescription = null,
                        tint = AppTheme.theme.colors.primaryText,
                        modifier = Modifier
                            .size(MediumIconSize)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun LabeledTextCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            LabeledTextWithIconCell(newLabeledTextCell(shape = CornersShape.TOP))
            LabeledTextWithIconCell(newLabeledTextCellWithLongText(shape = CornersShape.NONE))
            LabeledTextWithIconCell(
                newLabeledTextCell(
                    icon = AppIcons.Menu,
                    shape = CornersShape.BOTTOM
                )
            )
        }
    }
}

fun newLabeledTextCell(
    icon: ImageVector? = null,
    shape: CornersShape = CornersShape.ALL
) = LabeledTextWithIconCellViewModel(
    model = LabeledTextWithIconCellModel(
        id = "id",
        label = "Label",
        text = "Text",
        icon = icon,
        shape = shape
    ),
    intentProvider = PreviewIntentProvider
)

@Composable
fun newLabeledTextCellWithLongText(
    shape: CornersShape = CornersShape.ALL
): LabeledTextWithIconCellViewModel {
    return LabeledTextWithIconCellViewModel(
        model = LabeledTextWithIconCellModel(
            id = "id",
            label = "Label for long text",
            text = stringResource(R.string.long_dummy_text),
            icon = null,
            shape = shape
        ),
        intentProvider = PreviewIntentProvider
    )
}