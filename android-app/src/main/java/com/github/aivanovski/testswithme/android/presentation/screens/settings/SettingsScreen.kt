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
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.ScreenState
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newDividerCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newHeaderCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newHeaderWithDescriptionCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newSpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.events.SingleEventEffect
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.HalfMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.ui.SwitchCell
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.ui.newSwitchCell
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel.SwitchCellViewModel
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
}

@Composable
private fun CreateCell(viewModel: CellViewModel) {
    when (viewModel) {
        is SwitchCellViewModel -> SwitchCell(viewModel)
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
                screenState = null,
                viewModels = listOf(
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
                screenState = ScreenState.Loading
            ),
            onIntent = {}
        )
    }
}