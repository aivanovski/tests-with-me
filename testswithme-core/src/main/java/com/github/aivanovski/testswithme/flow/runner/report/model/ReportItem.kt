package com.github.aivanovski.testswithme.flow.runner.report.model

sealed interface ReportItem {

    data class StepItem(
        val step: String,
        val attemptCount: Int,
        val isSuccess: Boolean,
        val error: String? = null
    ) : ReportItem

    data class FlowItem(
        val name: String,
        val steps: List<ReportItem>,
        val isSuccess: Boolean,
        val error: String? = null,
        val stacktrace: String? = null
    ) : ReportItem
}