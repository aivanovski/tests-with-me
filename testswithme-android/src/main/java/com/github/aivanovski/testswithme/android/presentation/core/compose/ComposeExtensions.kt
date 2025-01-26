package com.github.aivanovski.testswithme.android.presentation.core.compose

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.CardCornerSize

@Composable
inline fun <T> rememberCallback(crossinline block: (T) -> Unit): (T) -> Unit {
    return remember { { value -> block.invoke(value) } }
}

@Composable
inline fun rememberOnClickedCallback(crossinline block: () -> Unit): () -> Unit {
    return remember { { block.invoke() } }
}

fun CornersShape.toComposeShape(): RoundedCornerShape =
    when (this) {
        CornersShape.TOP -> RoundedCornerShape(
            topStart = CardCornerSize,
            topEnd = CardCornerSize
        )

        CornersShape.BOTTOM -> RoundedCornerShape(
            bottomStart = CardCornerSize,
            bottomEnd = CardCornerSize
        )

        CornersShape.ALL -> RoundedCornerShape(
            size = CardCornerSize
        )

        CornersShape.NONE -> RoundedCornerShape(size = 0.dp)
    }

@Composable
fun TextSize.toTextStyle(): TextStyle =
    when (this) {
        TextSize.TITLE -> AppTheme.theme.typography.titleMedium
        TextSize.BODY_LARGE -> AppTheme.theme.typography.bodyLarge
        TextSize.BODY_MEDIUM -> AppTheme.theme.typography.bodyMedium
        TextSize.BODY_SMALL -> AppTheme.theme.typography.bodySmall
    }