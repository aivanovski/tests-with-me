package com.github.aivanovski.testswithme.android.presentation.screens.settings.cells

import com.github.aivanovski.testswithme.android.BuildConfig
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.DividerCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderWithDescriptionCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightAppColors
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.SwitchCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.model.TwoTextCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel.SwitchCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.viewModel.TwoTextCellViewModel

class SettingsCellFactory(
    private val resourceProvider: ResourceProvider
) {

    fun createCellViewModels(
        settings: Settings,
        isDriverRunning: Boolean,
        isGatewayRunning: Boolean,
        isGatewaySwitchEnabled: Boolean,
        intentProvider: CellIntentProvider
    ): List<CellViewModel> {
        return createModels(
            settings = settings,
            isDriverRunning = isDriverRunning,
            isGatewayRunning = isGatewayRunning,
            isGatewaySwitchEnabled = isGatewaySwitchEnabled
        ).map { model ->
            when (model) {
                is SwitchCellModel -> SwitchCellViewModel(model, intentProvider)
                is TwoTextCellModel -> TwoTextCellViewModel(model, intentProvider)
                else -> createCoreCellViewModel(model, intentProvider)
            }
        }
    }

    private fun createModels(
        settings: Settings,
        isDriverRunning: Boolean,
        isGatewayRunning: Boolean,
        isGatewaySwitchEnabled: Boolean
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        if (BuildConfig.DEBUG) {
            models.add(
                TwoTextCellModel(
                    id = CellId.SERVER_URL,
                    title = resourceProvider.getString(R.string.server_url),
                    description = settings.serverUrl
                )
            )
        }

        models.add(
            SwitchCellModel(
                id = CellId.SSL_VALIDATION_SWITCH,
                title = resourceProvider.getString(R.string.validate_ssl_certificates),
                description = resourceProvider.getString(R.string.requires_application_restart),
                isEnabled = true,
                isChecked = !settings.isSslVerificationDisabled
            )
        )

        models.addAll(
            listOf(
                SpaceCellModel(height = SmallMargin),
                DividerCellModel(
                    padding = ElementMargin,
                    color = LightAppColors.dividerOnPrimary
                ),
                SpaceCellModel(height = SmallMargin)
            )
        )

        models.add(
            HeaderWithDescriptionCellModel(
                id = CellId.DRIVER_DESCRIPTION,
                title = resourceProvider.getString(R.string.test_driver_title),
                description = resourceProvider.getString(R.string.test_driver_description)
            )
        )

        val driverStatus = if (isDriverRunning) {
            resourceProvider.getString(R.string.running_upper)
        } else {
            resourceProvider.getString(R.string.stopped_upper)
        }

        models.add(
            HeaderCellModel(
                id = CellId.DRIVER_BUTTON,
                title = resourceProvider.getString(R.string.driver_is, driverStatus),
                iconText = resourceProvider.getString(R.string.settings),
                icon = AppIcons.ArrowForward
            )
        )

        models.addAll(
            listOf(
                SpaceCellModel(height = SmallMargin),
                DividerCellModel(
                    padding = ElementMargin,
                    color = LightAppColors.dividerOnPrimary
                ),
                SpaceCellModel(height = SmallMargin)
            )
        )

        models.add(
            SwitchCellModel(
                id = CellId.GATEWAY_SWITCH,
                title = resourceProvider.getString(R.string.driver_gateway_title),
                description = resourceProvider.getString(R.string.driver_gateway_description),
                isChecked = isGatewayRunning,
                isEnabled = isGatewaySwitchEnabled
            )
        )

        return models
    }

    object CellId {
        const val SERVER_URL = "server_url"
        const val SSL_VALIDATION_SWITCH = "ssl_verification_switch"
        const val DRIVER_DESCRIPTION = "driver_description"
        const val DRIVER_BUTTON = "driver_button"
        const val GATEWAY_SWITCH = "gateway_switch"
    }
}