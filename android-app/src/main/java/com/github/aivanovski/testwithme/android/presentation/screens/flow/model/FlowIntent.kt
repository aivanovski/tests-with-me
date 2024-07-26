package com.github.aivanovski.testwithme.android.presentation.screens.flow.model

sealed interface FlowIntent {

    object Initialize : FlowIntent

    object ReBuildState : FlowIntent

    object OnDismissErrorDialog : FlowIntent

    object OnDismissFlowDialog : FlowIntent

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