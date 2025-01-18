package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TitleWithIconCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TitleWithIconCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TitleWithIconCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.MediumIconSize
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.MediumMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.toComposeShape

@Composable
fun TitleWithIconCell(viewModel: TitleWithIconCellViewModel) {
    val model = viewModel.model

    val onClick = rememberOnClickedCallback {
        viewModel.sendIntent(TitleWithIconCellIntent.OnIconClick(model.id))
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
        ) {
            Text(
                text = viewModel.model.title,
                color = AppTheme.theme.colors.primaryText,
                style = AppTheme.theme.typography.titleMedium,
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(
                        start = ElementMargin,
                        end = ElementMargin
                    )
                    .align(Alignment.CenterVertically)
            )

            if (model.icon != null) {
                Icon(
                    imageVector = model.icon,
                    contentDescription = null,
                    tint = AppTheme.theme.colors.primaryText,
                    modifier = Modifier
                        .padding(
                            end = MediumMargin
                        )
                        .size(MediumIconSize)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = onClick)
                )
            }
        }
    }
}

@Composable
@Preview
fun TitleWithIconCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        TitleWithIconCell(newTitleWithIconCellViewModel())
    }
}

fun newTitleWithIconCellViewModel(
    title: String = "Title",
    shape: CornersShape = CornersShape.ALL
) = TitleWithIconCellViewModel(
    model = TitleWithIconCellModel(
        id = "id",
        title = title,
        icon = AppIcons.Menu,
        shape = shape
    ),
    intentProvider = PreviewIntentProvider
)