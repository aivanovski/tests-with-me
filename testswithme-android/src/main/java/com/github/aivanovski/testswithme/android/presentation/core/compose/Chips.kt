package com.github.aivanovski.testswithme.android.presentation.core.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.toComposeColor
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.CardCornerSize
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.MediumIconSize
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.QuarterMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TinyMargin

@Composable
fun IconChip(
    icon: ImageVector,
    iconTint: Color,
    iconSize: Dp = MediumIconSize,
    text: String,
    textStyle: TextStyle = AppTheme.theme.typography.bodyMedium,
    textColor: Color = AppTheme.theme.colors.primaryText,
    cardColor: Color = AppTheme.theme.colors.cardOnPrimaryBackground
) {
    Card(
        shape = RoundedCornerShape(size = CardCornerSize),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(
                    horizontal = QuarterMargin,
                    vertical = TinyMargin
                )
                .defaultMinSize(
                    minWidth = 38.dp
                )
        ) {
            Icon(
                imageVector = icon,
                tint = iconTint,
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
            )

            Text(
                text = text,
                style = textStyle,
                color = textColor
            )
        }
    }
}

@Composable
fun TextChip(
    text: String,
    textSize: TextSize = TextSize.BODY_MEDIUM,
    textColor: Color = AppTheme.theme.colors.primaryText,
    cardColor: Color = AppTheme.theme.colors.cardOnPrimaryBackground,
    onClick: (() -> Unit)? = null
) {
    val cardModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Card(
        shape = RoundedCornerShape(size = CardCornerSize),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = cardModifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(
                    horizontal = QuarterMargin,
                    vertical = TinyMargin
                )
        ) {
            Text(
                text = text,
                style = textSize.toTextStyle(),
                color = textColor
            )
        }
    }
}

@Composable
@Preview
fun ChipPreview() {
    ThemedPreview(theme = LightTheme) {
        Column(
            modifier = Modifier
                .padding(all = ElementMargin)
        ) {
            IconChip(
                icon = AppIcons.CheckCircle,
                iconTint = IconTint.GREEN.toComposeColor(),
                text = "101"
            )

            Spacer(Modifier.height(SmallMargin))

            IconChip(
                icon = AppIcons.ErrorCircle,
                iconTint = IconTint.RED.toComposeColor(),
                text = "18"
            )

            Spacer(Modifier.height(SmallMargin))

            IconChip(
                icon = AppIcons.CheckCircle,
                iconTint = IconTint.PRIMARY_ICON.toComposeColor(),
                text = "18"
            )

            Spacer(Modifier.height(SmallMargin))

            TextChip(
                text = "256 executions"
            )

            Spacer(Modifier.height(SmallMargin))

            TextChip(
                text = "RUNNING",
                textColor = AppTheme.theme.colors.testGreen,
                cardColor = AppTheme.theme.colors.greenCard
            )

            Spacer(Modifier.height(SmallMargin))

            TextChip(
                text = "STOPPED",
                textColor = AppTheme.theme.colors.testRed,
                textSize = TextSize.TITLE,
                cardColor = AppTheme.theme.colors.redCard
            )
        }
    }
}