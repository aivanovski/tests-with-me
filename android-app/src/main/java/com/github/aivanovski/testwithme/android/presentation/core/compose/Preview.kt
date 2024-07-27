package com.github.aivanovski.testwithme.android.presentation.core.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.aivanovski.testwithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.Theme

@Composable
fun ThemedPreview(
    theme: Theme,
    background: Color = theme.colors.background,
    content: @Composable () -> Unit
) {
    AppTheme(theme = theme) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = background
                )
        ) {
            content.invoke()
        }
    }
}

@Composable
fun ThemedScreenPreview(
    theme: Theme,
    background: Color = theme.colors.background,
    content: @Composable () -> Unit
) {
    AppTheme(theme = theme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = background
                )
        ) {
            content.invoke()
        }
    }
}

object PreviewIntentProvider : CellIntentProvider {
    override fun subscribe(
        subscriber: Any,
        listener: (intent: BaseCellIntent) -> Unit
    ) {
    }

    override fun unsubscribe(subscriber: Any) {
    }

    override fun sendIntent(intent: BaseCellIntent) {
    }

    override fun isSubscribed(subscriber: Any): Boolean {
        return false
    }

    override fun clear() {
    }
}