package com.github.aivanovski.testswithme.entity

import kotlinx.serialization.Serializable

@Serializable
sealed interface FlowStep {

    @Serializable
    data class Launch(
        val packageName: String
    ) : FlowStep

    @Serializable
    data class SendBroadcast(
        val packageName: String,
        val action: String,
        val data: Map<String, String>,
        val condition: FlowStepPrecondition? = null
    ) : FlowStep

    @Serializable
    data class TapOn(
        val element: UiElementSelector,
        val isLong: Boolean = false,
        val condition: FlowStepPrecondition? = null
    ) : FlowStep

    @Serializable
    data class AssertVisible(
        val elements: List<UiElementSelector>
    ) : FlowStep

    @Serializable
    data class AssertNotVisible(
        val elements: List<UiElementSelector>
    ) : FlowStep

    @Serializable
    data class InputText(
        val text: String,
        val element: UiElementSelector? = null,
        val condition: FlowStepPrecondition? = null
    ) : FlowStep

    @Serializable
    data class PressKey(
        val key: KeyCode,
        val condition: FlowStepPrecondition? = null
    ) : FlowStep

    @Serializable
    data class WaitUntil(
        val conditionType: ConditionType,
        val element: UiElementSelector,
        val step: Duration,
        val timeout: Duration
    ) : FlowStep

    @Serializable
    data class RunFlow(
        val path: String,
        val condition: FlowStepPrecondition? = null
    ) : FlowStep
}