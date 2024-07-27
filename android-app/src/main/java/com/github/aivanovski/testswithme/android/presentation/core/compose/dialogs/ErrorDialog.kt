package com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.entity.ErrorMessage
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import java.lang.Exception

@Composable
fun ErrorDialog(
    message: ErrorMessage,
    onDismiss: () -> Unit
) {
    var isVisible by remember {
        mutableStateOf(true)
    }

    AlertDialog(
        onDismissRequest = {
            isVisible = false
            onDismiss.invoke()
        },
        text = {
            Text(
                text = message.message,
                color = AppTheme.theme.colors.primaryText,
                style = AppTheme.theme.typography.bodyLarge
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    isVisible = false
                    onDismiss.invoke()
                }
            ) {
                Text(
                    text = stringResource(R.string.ok)
                )
            }
        }
    )
}

@Composable
@Preview
fun ErrorDialogPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        ErrorDialog(
            message = newErrorMessage(),
            onDismiss = {}
        )
    }
}

@Composable
fun newErrorMessage(): ErrorMessage {
    return ErrorMessage(
        message = stringResource(R.string.error_has_been_occurred),
        cause = Exception()
    )
}