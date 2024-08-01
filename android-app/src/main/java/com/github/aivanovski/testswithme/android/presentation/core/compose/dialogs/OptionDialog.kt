package com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.OptionDialogState
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.DialogCardCornerSize
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineSmallItemHeight

@Composable
fun OptionDialog(
    state: OptionDialogState,
    onDismiss: () -> Unit,
    onClick: (action: DialogAction) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(DialogCardCornerSize)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                for ((option, action) in state.options.zip(state.actions)) {
                    OptionItem(
                        option = option,
                        action = action,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionItem(
    option: String,
    action: DialogAction,
    onClick: (action: DialogAction) -> Unit
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .clickable(
                onClick = {
                    onClick.invoke(action)
                }
            )
            .fillMaxWidth()
            .defaultMinSize(minHeight = TwoLineSmallItemHeight)
            .padding(
                horizontal = ElementMargin
            )
    ) {
        Text(
            text = option,
            style = AppTheme.theme.typography.titleMedium,
            color = AppTheme.theme.colors.primaryText,
            modifier = Modifier
        )
    }
}

@Composable
@Preview
fun OptionDialogPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        OptionDialog(
            state = newOptionDialog(),
            onDismiss = {},
            onClick = {}
        )
    }
}

fun newOptionDialog() =
    OptionDialogState(
        options = (1..3).map { "Option $it" },
        actions = (1..3).map { DialogAction(it) }
    )