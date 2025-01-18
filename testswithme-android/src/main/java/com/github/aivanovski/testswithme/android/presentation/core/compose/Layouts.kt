package com.github.aivanovski.testswithme.android.presentation.core.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CenteredBox(content: @Composable BoxScope.() -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        content = content,
        modifier = Modifier
            .fillMaxSize()
    )
}