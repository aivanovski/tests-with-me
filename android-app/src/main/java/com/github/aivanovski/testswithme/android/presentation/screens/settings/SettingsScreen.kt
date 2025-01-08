package com.github.aivanovski.testswithme.android.presentation.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.CreateCoreCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newDividerCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newHeaderCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newHeaderWithDescriptionCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newSpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.OptionDialog
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction
import com.github.aivanovski.testswithme.android.presentation.core.compose.events.SingleEventEffect
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.HalfMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.ui.SwitchCell
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.ui.TwoTextCell
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.ui.newSwitchCell
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.ui.newTwoTextCell
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel.SwitchCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel.TwoTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsState
import com.github.aivanovski.testswithme.android.presentation.screens.settings.model.SettingsUiEvent
import com.github.aivanovski.testswithme.android.utils.IntentUtils.newAccessibilityServicesIntent

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsState()

    SettingsScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )

    val context = LocalContext.current

    SingleEventEffect(
        eventFlow = viewModel.events,
        collector = { event ->
            when (event) {
                SettingsUiEvent.ShowAccessibilityServices -> {
                    context.startActivity(newAccessibilityServicesIntent())
                }
            }
        }
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
        CellsScreen(
            state = state,
            cellFactory = { viewModel -> CreateCell(viewModel) }
        )
    }

    if (state.optionDialogState != null) {
        val onDismiss = rememberOnClickedCallback {
            onIntent.invoke(SettingsIntent.OnDismissOptionDialog)
        }

        val onClick = rememberCallback { action: DialogAction ->
            onIntent.invoke(SettingsIntent.OnOptionDialogClick(action))
        }

        OptionDialog(
            state = state.optionDialogState,
            onDismiss = onDismiss,
            onClick = onClick
        )
    }
}

@Composable
private fun CreateCell(viewModel: CellViewModel) {
    when (viewModel) {
        is SwitchCellViewModel -> SwitchCell(viewModel)
        is TwoTextCellViewModel -> TwoTextCell(viewModel)
        else -> CreateCoreCell(viewModel)
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
                terminalState = null,
                viewModels = listOf(
                    newTwoTextCell(
                        title = stringResource(R.string.server_url),
                        description = "https://testswithme.org"
                    ),
                    newSwitchCell(
                        title = stringResource(R.string.validate_ssl_certificates),
                        description = stringResource(R.string.requires_application_restart)
                    ),

                    newSpaceCell(height = HalfMargin),
                    newDividerCell(),
                    newSpaceCell(height = HalfMargin),

                    newHeaderWithDescriptionCell(
                        title = stringResource(R.string.test_driver_title),
                        description = stringResource(R.string.test_driver_description)
                    ),
                    newHeaderCell(
                        title = "Driver is STOPPED",
                        iconText = stringResource(R.string.settings),
                        icon = AppIcons.ArrowForward
                    ),

                    newSpaceCell(height = HalfMargin),
                    newDividerCell(),
                    newSpaceCell(height = HalfMargin),

                    newSwitchCell(
                        title = stringResource(R.string.driver_gateway_title),
                        description = stringResource(R.string.driver_gateway_description)
                    ),

                    newSpaceCell(height = HalfMargin),
                    newDividerCell(),
                    newSpaceCell(height = HalfMargin),

                    newHeaderWithDescriptionCell(
                        title = stringResource(R.string.flakiness_configuration),
                        description = stringResource(R.string.flakiness_configuration_description)
                    ),
                    newTwoTextCell(
                        title = stringResource(R.string.delay_scale_factor_title),
                        description = "1x"
                    ),
                    newTwoTextCell(
                        title = stringResource(R.string.number_of_retries_title),
                        description = "3"
                    )
                )
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
                terminalState = TerminalState.Loading
            ),
            onIntent = {}
        )
    }
}