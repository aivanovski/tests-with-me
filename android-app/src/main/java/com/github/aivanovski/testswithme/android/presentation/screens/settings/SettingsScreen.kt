package com.github.aivanovski.testswithme.android.presentation.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.compose.ProgressIndicator
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.QuarterMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.TwoLineMediumItemHeight
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsIntent.OnSslValidationStateChanged
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsState

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsState()

    SettingsScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun SettingsScreen(
    state: SettingsState,
    onIntent: (intent: SettingsIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
            state.isLoading -> {
                ProgressIndicator()
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    SwitchItem(
                        title = stringResource(R.string.validate_ssl_certificates),
                        description = stringResource(R.string.requires_application_restart),
                        isEnabled = state.isSslValidationEnabled,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}

@Composable
private fun SwitchItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onIntent: (intent: SettingsIntent) -> Unit
) {
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
        var isEnabledState by remember {
            mutableStateOf(isEnabled)
        }

        val onChecked = rememberCallback { isChecked: Boolean ->
            isEnabledState = isChecked
            onIntent.invoke(OnSslValidationStateChanged(isChecked))
        }

        Column(
            modifier = Modifier
                .weight(weight = 1f)
        ) {
            Text(
                text = title,
                color = AppTheme.theme.colors.primaryText,
                style = AppTheme.theme.typography.bodyLarge
            )

            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    color = AppTheme.theme.colors.secondaryText,
                    style = AppTheme.theme.typography.bodyMedium
                )
            }
        }

        Switch(
            checked = isEnabledState,
            onCheckedChange = onChecked
        )
    }
}

@Preview
@Composable
fun SettingsScreenDataPreview() {
    ThemedScreenPreview(
        theme = LightTheme
    ) {
        SettingsScreen(
            state = SettingsState(
                isLoading = false,
                isSslValidationEnabled = true
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
fun SettingsScreenLoadingPreview() {
    ThemedScreenPreview(
        theme = LightTheme
    ) {
        SettingsScreen(
            state = SettingsState(
                isLoading = true,
                isSslValidationEnabled = true
            ),
            onIntent = {}
        )
    }
}