package com.github.aivanovski.testswithme.android.presentation.core.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.DarkTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme

@Composable
fun ProgressIndicator(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(
        modifier = modifier
    ) {
        val isInEditMode = LocalView.current.isInEditMode
        if (isInEditMode) {
            CircularProgressIndicator(
                progress = 0.75f,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
fun LightProgressPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        ProgressIndicator()
    }
}

@Preview
@Composable
fun DakrProgressPreview() {
    ThemedScreenPreview(theme = DarkTheme) {
        ProgressIndicator()
    }
}