package com.github.aivanovski.testwithme.android.presentation.core.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.github.aivanovski.testwithme.android.R
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme

@Composable
fun EmptyMessage(message: String) {
    Text(
        text = message,
        fontSize = 24.sp, // TODO: Font size
        textAlign = TextAlign.Center,
        color = AppTheme.theme.colors.primaryText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = GroupMargin,
                vertical = GroupMargin
            )
    )
}

@Composable
@Preview
fun EmptyMessagePreview() {
    ThemedPreview(theme = LightTheme) {
        EmptyMessage(
            message = stringResource(R.string.no_tests)
        )
    }
}