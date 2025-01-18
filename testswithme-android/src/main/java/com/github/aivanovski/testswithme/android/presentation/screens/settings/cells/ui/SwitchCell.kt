package com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.compose.ElementSpace
import com.github.aivanovski.testswithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.QuarterMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineMediumItemHeight
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.SwitchCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.SwitchCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel.SwitchCellViewModel

@Composable
fun SwitchCell(viewModel: SwitchCellViewModel) {
    val model by viewModel.observableModel.collectAsState()

    var isCheckedCached by remember {
        mutableStateOf(model.isChecked)
    }

    val onChecked = rememberCallback { isChecked: Boolean ->
        isCheckedCached = isChecked
        viewModel.sendIntent(
            SwitchCellIntent.OnCheckChanged(
                cellId = model.id,
                isChecked = isChecked
            )
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = ElementMargin,
                vertical = QuarterMargin
            )
            .defaultMinSize(minHeight = TwoLineMediumItemHeight)
    ) {
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(end = SmallMargin)
        ) {
            Text(
                text = model.title,
                color = AppTheme.theme.colors.primaryText,
                style = AppTheme.theme.typography.titleMedium
            )

            if (model.description.isNotEmpty()) {
                Text(
                    text = model.description,
                    color = AppTheme.theme.colors.secondaryText,
                    style = AppTheme.theme.typography.bodyMedium
                )
            }
        }

        Switch(
            checked = isCheckedCached,
            enabled = model.isEnabled,
            onCheckedChange = onChecked
        )
    }
}

@Composable
@Preview
fun SwitchCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            SwitchCell(newSwitchCell(isChecked = true, isEnabled = true))
            ElementSpace()
            SwitchCell(newSwitchCell(isChecked = true, isEnabled = false))
            ElementSpace()
            SwitchCell(newSwitchCell(isChecked = false, isEnabled = true))
            ElementSpace()
            SwitchCell(newSwitchCell(isChecked = false, isEnabled = false))
        }
    }
}

fun newSwitchCell(
    title: String = "Title",
    description: String = "Description",
    isChecked: Boolean = false,
    isEnabled: Boolean = true
) = SwitchCellViewModel(
    model = SwitchCellModel(
        id = "id",
        title = title,
        description = description,
        isChecked = isChecked,
        isEnabled = isEnabled
    ),
    intentProvider = PreviewIntentProvider
)