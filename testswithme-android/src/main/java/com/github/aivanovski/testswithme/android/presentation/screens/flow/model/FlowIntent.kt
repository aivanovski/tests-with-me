package com.github.aivanovski.testswithme.android.presentation.screens.flow.model

import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction

sealed interface FlowIntent {

    data object Initialize : FlowIntent

    data object ReBuildState : FlowIntent

    data object OnDismissErrorDialog : FlowIntent

    data object OnDismissFlowDialog : FlowIntent

    data object OnUploadButtonClick : FlowIntent

    data object OnDismissOptionDialog : FlowIntent

    data class OnOptionDialogClick(
        val action: DialogAction
    ) : FlowIntent

    data class OnFlowDialogActionClick(
        val actionId: Int
    ) : FlowIntent

    data class RunFlow(
        val flowUid: String
    ) : FlowIntent

    data class RunFlows(
        val flowUids: List<String>
    ) : FlowIntent

    data class OnFlowClick(
        val flowUid: String
    ) : FlowIntent
}