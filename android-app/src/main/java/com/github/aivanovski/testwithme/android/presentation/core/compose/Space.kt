package com.github.aivanovski.testwithme.android.presentation.core.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin

@Composable
fun ElementSpace() {
    Spacer(modifier = Modifier.height(ElementMargin))
}
