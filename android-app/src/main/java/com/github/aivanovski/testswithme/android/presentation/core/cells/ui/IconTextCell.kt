package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.clickable
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
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTextCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.toComposeColor
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.IconTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.CardCornerSize
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.HalfMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineSmallItemHeight

@Composable
fun IconTextCell(viewModel: IconTextCellViewModel) {
    val model = viewModel.model

    val onClick = rememberOnClickedCallback {
        viewModel.sendIntent(IconTextCellIntent.OnClick(model.id))
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
                .height(height = TwoLineSmallItemHeight)
        ) {
            Icon(
                imageVector = model.icon,
                tint = model.iconTint.toComposeColor(),
                contentDescription = null
            )

            Text(
                text = model.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = AppTheme.theme.colors.primaryText,
                style = AppTheme.theme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HalfMargin)
            )
        }
    }
}

@Composable
@Preview
fun IconTextCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        IconTextCell(newIconTextCellViewModel())
    }
}

fun newIconTextCellViewModel() =
    IconTextCellViewModel(
        model = IconTextCellModel(
            id = "id",
            title = "Title",
            icon = AppIcons.CheckCircle,
            iconTint = IconTint.GREEN
        ),
        intentProvider = PreviewIntentProvider
    )