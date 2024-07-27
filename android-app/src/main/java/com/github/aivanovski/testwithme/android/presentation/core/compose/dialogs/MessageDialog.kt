package com.github.aivanovski.testwithme.android.presentation.core.compose.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.dialogs.model.MessageDialogButton
import com.github.aivanovski.testwithme.android.presentation.core.compose.dialogs.model.MessageDialogIntent
import com.github.aivanovski.testwithme.android.presentation.core.compose.dialogs.model.MessageDialogState
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme

@Composable
fun MessageDialog(
    state: MessageDialogState,
    onIntent: (intent: MessageDialogIntent) -> Unit
) {
    val actionButton = state.actionButton as? MessageDialogButton.ActionButton

    AlertDialog(
        onDismissRequest = { // TODO: optimize
            onIntent.invoke(MessageDialogIntent.OnDismiss)
        },
        title = if (state.title != null) {
            {
                Text(
                    text = state.title,
                    style = AppTheme.theme.typography.titleMedium
                )
            }
        } else {
            null
        },
        text = {
            Text(
                text = state.message,
                style = AppTheme.theme.typography.bodyLarge
            )
        },
        confirmButton = {
            if (actionButton != null) {
                Button(
                    onClick = { // TODO: optimize
                        val intent = MessageDialogIntent.OnActionButtonClick(
                            actionId = state.actionButton.actionId
                        )
                        onIntent.invoke(intent)
                    }
                ) {
                    Text(text = actionButton.title)
                }
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = state.isCancellable,
            dismissOnClickOutside = state.isCancellable
        )
    )
}

@Composable
@Preview
fun MessageDialogPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        MessageDialog(
            state = newMessageDialog(),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun MessageDialogWithButtonPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        MessageDialog(
            state = newMessageDialogWithButton(),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun MessageDialogWithTitlePreview() {
    ThemedScreenPreview(theme = LightTheme) {
        MessageDialog(
            state = newMessageDialogWithTitle(),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun MessageDialogWithButtonAndTitlePreview() {
    ThemedScreenPreview(theme = LightTheme) {
        MessageDialog(
            state = newMessageDialogWithButtonAndTitle(),
            onIntent = {}
        )
    }
}

fun newMessageDialog(): MessageDialogState {
    return MessageDialogState(
        title = null,
        message = "Message"
    )
}

fun newMessageDialogWithButton(): MessageDialogState {
    return MessageDialogState(
        title = null,
        message = "Message",
        actionButton = MessageDialogButton.ActionButton("Confirm", 0)
    )
}

fun newMessageDialogWithTitle(): MessageDialogState {
    return MessageDialogState(
        title = "Title",
        message = "Message"
    )
}

fun newMessageDialogWithButtonAndTitle(): MessageDialogState {
    return MessageDialogState(
        title = "Title",
        message = "Message",
        actionButton = MessageDialogButton.ActionButton("Confirm", 0)
    )
}