package com.github.aivanovski.testswithme.android.presentation.screens.testContent.cells

import androidx.compose.ui.graphics.Color
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.ExecutionResult
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.android.entity.db.StepEntry
import com.github.aivanovski.testswithme.android.extensions.getChipBackgroundColor
import com.github.aivanovski.testswithme.android.extensions.getChipTextColor
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.EmptyTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ShapedSpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TwoLineTextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.QuarterMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentData
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentScreenMode
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import com.github.aivanovski.testswithme.entity.StepResult
import com.github.aivanovski.testswithme.flow.formatter.FlowStepFormatter
import com.github.aivanovski.testswithme.flow.formatter.FlowStepFormatter.Format
import com.github.aivanovski.testswithme.flow.runner.report.model.ReportItem
import com.github.aivanovski.testswithme.utils.StringUtils

class TestContentCellFactory(
    private val resourceProvider: ResourceProvider,
    private val themeProvider: ThemeProvider,
    private val jsonSerializer: JsonSerializer
) {

    private val stepFormatter = FlowStepFormatter()

    fun createCellViewModels(
        data: TestContentData,
        mode: TestContentScreenMode,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return createModels(data, mode)
            .map { model ->
                when (model) {
                    else -> createCoreCellViewModel(model, intentProvider)
                }
            }
    }

    private fun createModels(
        data: TestContentData,
        mode: TestContentScreenMode
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        if (mode !is TestContentScreenMode.FlowContent) {
            val executionResult = getFlowExecutionResult(data)
            models.add(SpaceCellModel(GroupMargin))
            models.addAll(createStatusModels(executionResult, data.parsedReport))
        }

        if (data.report != null) {
            if (models.isEmpty()) {
                models.add(SpaceCellModel(GroupMargin))
            }

            models.addAll(createReportModels())
        }

        models.addAll(
            createStepModels(
                steps = data.flow.steps,
                localRuns = data.localRuns,
                parsedReport = data.parsedReport
            )
        )

        models.add(SpaceCellModel(GroupMargin))

        return models
    }

    private fun getFlowExecutionResult(data: TestContentData): ExecutionResult {
        val isPassed = when {
            data.remoteRun != null -> data.remoteRun.isSuccess
            data.job != null -> data.job.executionResult == ExecutionResult.SUCCESS
            data.parsedReport != null -> data.parsedReport.isSuccess
            else -> null
        }

        return ExecutionResult.fromBoolean(isPassed)
    }

    private fun createStatusModels(
        executionResult: ExecutionResult,
        parsedReport: ReportItem.FlowItem?
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        val hasErrorMessage = (
            executionResult == ExecutionResult.FAILED &&
                parsedReport?.error != null
            )
        val hasErrorStacktrace = (
            executionResult == ExecutionResult.FAILED &&
                parsedReport?.stacktrace != null
            )

        val chipText = when (executionResult) {
            ExecutionResult.SUCCESS -> resourceProvider.getString(R.string.passed).uppercase()
            else -> resourceProvider.getString(R.string.failed).uppercase()
        }
        models.add(
            TextWithChipCellModel(
                id = CellId.STATUS,
                text = resourceProvider.getString(R.string.test_status),
                textSize = TextSize.TITLE,
                chipText = chipText,
                chipTextColor = executionResult.getChipTextColor(themeProvider),
                chipColor = executionResult.getChipBackgroundColor(themeProvider),
                shape = if (!hasErrorMessage) CornersShape.ALL else CornersShape.TOP
            )
        )

        if (hasErrorMessage) {
            models.add(
                TextCellModel(
                    id = CellId.ERROR_TITLE,
                    text = resourceProvider.getString(R.string.error_message_with_colon),
                    textColor = themeProvider.theme.colors.testRed,
                    textSize = TextSize.BODY_LARGE,
                    shape = CornersShape.NONE
                )
            )

            val errorMessage = parsedReport?.error ?: StringUtils.EMPTY
            val stacktrace = parsedReport?.stacktrace ?: StringUtils.EMPTY

            if (errorMessage.isNotEmpty()) {
                models.add(
                    TextCellModel(
                        id = CellId.ERROR_MESSAGE,
                        text = errorMessage,
                        textColor = themeProvider.theme.colors.testRed,
                        textSize = TextSize.BODY_LARGE,
                        shape = CornersShape.NONE
                    )
                )
            }

            if (stacktrace.isNotEmpty()) {
                if (errorMessage.isNotEmpty()) {
                    models.add(
                        ShapedSpaceCellModel(
                            height = QuarterMargin,
                            shape = CornersShape.NONE
                        )
                    )
                }

                val shortStacktrace = stacktrace.split(StringUtils.NEW_LINE)
                    .take(3)
                    .joinToString(StringUtils.NEW_LINE)
                    .plus("\n...")

                models.add(
                    TextCellModel(
                        id = CellId.ERROR_STACKTRACE,
                        text = shortStacktrace,
                        textColor = themeProvider.theme.colors.secondaryText,
                        textSize = TextSize.BODY_SMALL,
                        shape = CornersShape.NONE
                    )
                )
            }

            models.add(
                ShapedSpaceCellModel(
                    height = ElementMargin,
                    shape = CornersShape.BOTTOM
                )
            )
        }

        if (hasErrorStacktrace) {
            models.add(
                HeaderCellModel(
                    id = CellId.ERROR_HEADER,
                    title = StringUtils.EMPTY,
                    iconText = resourceProvider.getString(R.string.view_error),
                    icon = AppIcons.ArrowForward
                )
            )
        }

        return models
    }

    private fun createReportModels(): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        models.add(
            HeaderCellModel(
                id = CellId.REPORT_HEADER,
                title = StringUtils.EMPTY,
                iconText = resourceProvider.getString(R.string.view_report),
                icon = AppIcons.ArrowForward
            )
        )

        return models
    }

    private fun createStepModels(
        steps: List<StepEntry>,
        localRuns: List<LocalStepRun>,
        parsedReport: ReportItem.FlowItem?
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        if (steps.isNotEmpty()) {
            models.add(
                HeaderCellModel(
                    id = CellId.STEPS_HEADER,
                    title = resourceProvider.getString(R.string.steps),
                    iconText = null,
                    icon = null
                )
            )

            val stepUidToRunMap = localRuns.associateBy { run -> run.stepUid }

            for ((index, step) in steps.withIndex()) {
                val run = stepUidToRunMap[step.uid]

                val shape = when {
                    steps.size == 1 -> CornersShape.ALL
                    index == 0 -> CornersShape.TOP
                    index == steps.lastIndex -> CornersShape.BOTTOM
                    else -> CornersShape.NONE
                }

                val descriptionLines = stepFormatter.format(step.command, Format.FULL)
                    .split(StringUtils.NEW_LINE)

                val title = descriptionLines.first()
                val description = descriptionLines.takeLast(descriptionLines.size - 1)
                    .filter { line -> line.isNotEmpty() }
                    .joinToString(separator = StringUtils.NEW_LINE)

                val runResult = run?.result?.let { resultJson ->
                    StepResult.deserialize(jsonSerializer, resultJson).getOrNull()
                }
                val reportResult = parsedReport?.steps?.getOrNull(index)

                val isSuccess: Boolean? = when {
                    runResult != null -> runResult.isSuccess
                    reportResult is ReportItem.FlowItem -> reportResult.isSuccess
                    reportResult is ReportItem.StepItem -> reportResult.isSuccess
                    else -> null
                }

                val chipText = when (isSuccess) {
                    true -> resourceProvider.getString(R.string.passed).uppercase()
                    false -> resourceProvider.getString(R.string.failed).uppercase()
                    else -> StringUtils.EMPTY
                }

                val chipColor = when (isSuccess) {
                    true -> themeProvider.theme.colors.greenCard
                    false -> themeProvider.theme.colors.redCard
                    else -> Color.Unspecified
                }

                val chipTextColor = when (isSuccess) {
                    true -> themeProvider.theme.colors.testGreen
                    false -> themeProvider.theme.colors.testRed
                    else -> Color.Unspecified
                }

                models.add(
                    TwoLineTextWithChipCellModel(
                        id = step.uid,
                        title = title,
                        description = description,
                        shape = shape,
                        chipText = chipText,
                        chipTextColor = chipTextColor,
                        chipColor = chipColor
                    )
                )
            }
        } else {
            models.add(
                EmptyTextCellModel(
                    id = CellId.EMPTY_MESSAGE,
                    message = resourceProvider.getString(R.string.no_steps)
                )
            )
        }

        return models
    }

    object CellId {
        const val STATUS = "status"
        const val ERROR_TITLE = "error-title"
        const val ERROR_MESSAGE = "error-message"
        const val ERROR_STACKTRACE = "error-stacktrace"

        const val STEPS_HEADER = "steps-header"
        const val REPORT_HEADER = "report-header"
        const val ERROR_HEADER = "error-header"

        const val EMPTY_MESSAGE = "empty-message"
    }
}