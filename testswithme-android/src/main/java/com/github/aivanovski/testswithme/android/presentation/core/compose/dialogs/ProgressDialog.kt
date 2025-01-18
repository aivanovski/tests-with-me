package com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme

@Composable
fun ProgressDialog(
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    var isVisible by remember {
        mutableStateOf(true)
    }

    AlertDialog(
        onDismissRequest = {
            isVisible = false
            onButtonClick.invoke()
        },
        text = {
            Text(
                text = message,
                textAlign = TextAlign.Center,
                color = AppTheme.theme.colors.primaryText,
                style = AppTheme.theme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    isVisible = false
                    onButtonClick.invoke()
                }
            ) {
                Text(
                    text = buttonText
                )
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}

@Composable
@Preview
fun ProgressDialogPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        ProgressDialog(
            message = "Preparing flow data...",
            buttonText = stringResource(R.string.cancel),
            onButtonClick = {}
        )
    }
}