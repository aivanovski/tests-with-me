package com.github.aivanovski.testswithme.android.presentation.screens.flow

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.CreateCoreCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.CellsScreen
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newEmptyTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newHeaderCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newSpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.ErrorDialog
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.MessageDialog
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.OptionDialog
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogIntent
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogState
import com.github.aivanovski.testswithme.android.presentation.core.compose.events.SingleEventEffect
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.HalfMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.ui.HistoryItemCell
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.ui.newFailedHistoryItemCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.ui.newSuccessHistoryItemCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.viewModel.HistoryItemCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowIntent
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowState
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowUiEvent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.ui.FlowCell
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.FlowCellViewModel
import com.github.aivanovski.testswithme.android.utils.IntentUtils.newAccessibilityServicesIntent
import com.github.aivanovski.testswithme.android.utils.IntentUtils.newOpenUrlIntent

@Composable
fun FlowScreen(viewModel: FlowViewModel) {
    val state by viewModel.state.collectAsState()

    FlowScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )

    val context = LocalContext.current

    SingleEventEffect(
        eventFlow = viewModel.events,
        collector = { event ->
            when (event) {
                FlowUiEvent.ShowAccessibilityServices -> {
                    context.startActivity(newAccessibilityServicesIntent())
                }

                is FlowUiEvent.OpenUrl -> {
                    val intent = Intent.createChooser(newOpenUrlIntent(event.url), null)
                    context.startActivity(intent)
                }
            }
        }
    )
}

@Composable
private fun FlowScreen(
    state: FlowState,
    onIntent: (intent: FlowIntent) -> Unit
) {
    val onAddClick = rememberOnClickedCallback {
        onIntent.invoke(FlowIntent.OnUploadButtonClick)
    }

    Surface(
        color = AppTheme.theme.colors.secondaryBackground
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CellsScreen(
                state = state,
                cellFactory = { cellViewModel ->
                    CreateCell(cellViewModel)
                }
            )

            if (state.isUploadButtonVisible) {
                FloatingActionButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = ElementMargin,
                            bottom = ElementMargin
                        )
                ) {
                    Icon(
                        imageVector = AppIcons.Upload,
                        tint = AppTheme.theme.colors.primaryText,
                        contentDescription = null
                    )
                }
            }
        }

        if (state.terminalState == null) {
            if (state.errorDialogMessage != null) {
                ErrorDialog(
                    message = state.errorDialogMessage,
                    onDismiss = { // TODO: optimize
                        onIntent.invoke(FlowIntent.OnDismissErrorDialog)
                    }
                )
            }

            if (state.flowDialogState != null) {
                FlowDialogContent(
                    state = state.flowDialogState,
                    onIntent = onIntent
                )
            }

            if (state.optionDialogState != null) {
                val onDismiss = rememberOnClickedCallback {
                    onIntent.invoke(FlowIntent.OnDismissOptionDialog)
                }

                val onClick = rememberCallback { action: DialogAction ->
                    onIntent.invoke(FlowIntent.OnOptionDialogClick(action))
                }

                OptionDialog(
                    state = state.optionDialogState,
                    onDismiss = onDismiss,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
private fun FlowDialogContent(
    state: MessageDialogState,
    onIntent: (intent: FlowIntent) -> Unit
) {
    MessageDialog(
        state = state,
        onIntent = { dialogIntent -> // TODO: optimize
            when (dialogIntent) {
                is MessageDialogIntent.OnDismiss -> {
                    onIntent.invoke(FlowIntent.OnDismissFlowDialog)
                }

                is MessageDialogIntent.OnActionButtonClick -> {
                    onIntent.invoke(
                        FlowIntent.OnFlowDialogActionClick(
                            actionId = dialogIntent.actionId
                        )
                    )
                }
            }
        }
    )
}

@Composable
private fun CreateCell(viewModel: CellViewModel) {
    when (viewModel) {
        is HistoryItemCellViewModel -> HistoryItemCell(viewModel)
        is FlowCellViewModel -> FlowCell(viewModel)
        else -> CreateCoreCell(viewModel)
    }
}

@Composable
@Preview
fun FlowScreenPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        FlowScreen(
            state = FlowState(
                viewModels = listOf(
                    newSpaceCell(GroupMargin),
                    newSpaceCell(ElementMargin),
                    newHeaderCell(),
                    newSuccessHistoryItemCellViewModel(),
                    newSpaceCell(HalfMargin),
                    newFailedHistoryItemCellViewModel(),
                    newSpaceCell(HalfMargin),
                    newSuccessHistoryItemCellViewModel()
                ),
                errorDialogMessage = null,
                flowDialogState = null
            ),
            onIntent = {}
        )
    }
}

@Composable
@Preview
fun FlowScreenWithoutRunsPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        FlowScreen(
            state = FlowState(
                viewModels = listOf(
                    newSpaceCell(GroupMargin),
                    newEmptyTextCellViewModel()
                ),
                errorDialogMessage = null,
                flowDialogState = null
            ),
            onIntent = {}
        )
    }
}