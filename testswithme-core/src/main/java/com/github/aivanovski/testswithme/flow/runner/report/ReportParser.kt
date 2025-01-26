package com.github.aivanovski.testswithme.flow.runner.report

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.extensions.toIntSafely
import com.github.aivanovski.testswithme.flow.runner.report.model.ReportItem
import com.github.aivanovski.testswithme.flow.runner.report.model.ReportItem.FlowItem
import com.github.aivanovski.testswithme.flow.runner.report.model.exception.EmptyParameterException
import com.github.aivanovski.testswithme.flow.runner.report.model.exception.InvalidFlowNameException
import com.github.aivanovski.testswithme.flow.runner.report.model.exception.InvalidLineFormatException
import com.github.aivanovski.testswithme.flow.runner.report.model.exception.InvalidParserStateException
import com.github.aivanovski.testswithme.flow.runner.report.model.exception.ReportParsingException
import com.github.aivanovski.testswithme.flow.yaml.extensions.isBlank
import com.github.aivanovski.testswithme.flow.yaml.model.TextLine
import com.github.aivanovski.testswithme.utils.StringUtils
import java.util.Deque
import java.util.LinkedList
import java.util.Queue
import java.util.TreeMap

class ReportParser {

    fun parse(content: String): Either<ReportParsingException, FlowItem> =
        either {
            val values = ValueHolder(LinkedList(), LinkedHashMap())

            val lines: Queue<TextLine> = LinkedList<TextLine>()
                .apply {
                    addAll(
                        content.split(StringUtils.NEW_LINE)
                            .mapIndexed { index, line -> TextLine(index, line) }
                            .filter { line -> !line.isBlank() }
                    )
                }

            while (lines.isNotEmpty()) {
                val line = lines.poll()

                when {
                    REGEX_FLOW_STARTED.matches(line.text) ->
                        parseFlowStarted(values, line).bind()

                    REGEX_FLOW_PASSED.matches(line.text) ->
                        parseFlowPassed(values, line).bind()

                    REGEX_FLOW_FAILED.matches(line.text) ->
                        parseFlowFailed(values, line).bind()

                    REGEX_STEP_PASSED.matches(line.text) ->
                        parseStepPassed(values, line).bind()

                    REGEX_STEP_FAILED.matches(line.text) ->
                        parseStepFailed(values, line).bind()

                    REGEX_STEP_STARTED.matches(line.text) ->
                        parseStepStarted(values, line).bind()

                    REGEX_STEP_RESTARTED.matches(line.text) ->
                        parseStepRestarted(values, line).bind()

                    line.text.contains(EXCEPTION, ignoreCase = true) ->
                        parseStackTrace(values, lines, line).bind()
                }
            }

            val resultFlow = values.finished.values.lastOrNull()
                ?: raise(InvalidParserStateException())

            val steps = resultFlow.steps.values.toList()
                .toStepItems(flowNameToFlowMap = values.finished)
                .bind()

            FlowItem(
                name = resultFlow.name,
                steps = steps,
                isSuccess = resultFlow.isSuccess ?: false,
                error = resultFlow.error,
                stacktrace = resultFlow.stacktrace
            )
        }

    private fun List<MutableStep>.toStepItems(
        flowNameToFlowMap: Map<String, MutableFlow>
    ): Either<ReportParsingException, List<ReportItem>> {
        val steps = this

        return either {
            steps.map { step ->
                if (step.innerFlowName == null) {
                    ReportItem.StepItem(
                        step = step.step,
                        attemptCount = step.attemptCount,
                        isSuccess = step.isSuccess ?: false,
                        error = step.error
                    )
                } else {
                    val innerFlow = flowNameToFlowMap[step.innerFlowName]
                        ?: raise(InvalidParserStateException())

                    val innerSteps = innerFlow.steps.values
                        .toList()
                        .toStepItems(flowNameToFlowMap = flowNameToFlowMap)
                        .bind()

                    FlowItem(
                        name = step.innerFlowName,
                        steps = innerSteps,
                        isSuccess = innerFlow.isSuccess ?: false,
                        error = innerFlow.error,
                        stacktrace = innerFlow.stacktrace
                    )
                }
            }
        }
    }

    private fun parseFlowStarted(
        values: ValueHolder,
        line: TextLine
    ): Either<ReportParsingException, Unit> =
        either {
            val groups = REGEX_FLOW_STARTED.aggregateGroups(line.text)
            val flowName = groups[GROUP_FLOW_NAME]

            val flow = values.current.firstOrNull()
            if (flow != null && flow.name == flowName) {
                raise(ReportParsingException("Recursive flow reference"))
            }

            if (flowName.isNullOrBlank()) {
                raise(EmptyParameterException(line))
            }

            values.current.push(MutableFlow(name = flowName))
        }

    private fun parseFlowPassed(
        values: ValueHolder,
        line: TextLine
    ): Either<ReportParsingException, Unit> =
        either {
            val groups = REGEX_FLOW_PASSED.aggregateGroups(line.text)
            val flowName = groups[GROUP_FLOW_NAME]

            val currentFlow = values.current.pop()
                ?: raise(InvalidParserStateException(line))

            if (currentFlow.name != flowName) {
                raise(InvalidFlowNameException(flowName))
            }

            currentFlow.isSuccess = true

            values.finished[flowName] = currentFlow
        }

    private fun parseFlowFailed(
        values: ValueHolder,
        line: TextLine
    ): Either<ReportParsingException, Unit> =
        either {
            val groups = REGEX_FLOW_FAILED.aggregateGroups(line.text)
            val flowName = groups[GROUP_FLOW_NAME]
            val error = groups[GROUP_ERROR]

            val currentFlow = values.current.pop()
                ?: raise(InvalidParserStateException(line))

            if (currentFlow.name != flowName) {
                raise(InvalidFlowNameException(flowName))
            }

            currentFlow.error = error
            currentFlow.isSuccess = false

            values.finished[flowName] = currentFlow
        }

    private fun parseStepStarted(
        values: ValueHolder,
        line: TextLine
    ): Either<ReportParsingException, Unit> =
        either {
            val groups = REGEX_STEP_STARTED.aggregateGroups(line.text)

            val flowName = groups[GROUP_FLOW_NAME]
            val index = groups[GROUP_INDEX]?.toIntSafely()
            val stepText = groups[GROUP_STEP]

            if (flowName == null || index == null || stepText == null) {
                raise(InvalidLineFormatException(line))
            }

            val currentFlow = values.current.firstOrNull()
                ?: raise(InvalidParserStateException(line))

            val prevStepIndex = if (currentFlow.steps.isNotEmpty()) {
                currentFlow.steps.lastKey() ?: 0
            } else {
                0
            }

            if (prevStepIndex + 1 != index) {
                raise(InvalidParserStateException(line))
            }

            val innerFlowName = if (REGEX_RUN_FLOW.matches(stepText)) {
                val m = REGEX_RUN_FLOW.aggregateGroups(stepText)
                m[GROUP_FLOW_NAME]
            } else {
                null
            }

            val step = MutableStep(
                index = index,
                step = stepText,
                innerFlowName = innerFlowName,
                attemptCount = 0,
                isSuccess = null,
                error = null
            )

            currentFlow.steps[index] = step
        }

    private fun parseStepRestarted(
        values: ValueHolder,
        line: TextLine
    ): Either<ReportParsingException, Unit> =
        either {
            val groups = REGEX_STEP_STARTED.aggregateGroups(line.text)

            val flowName = groups[GROUP_FLOW_NAME]
            val index = groups[GROUP_INDEX]?.toIntSafely()
            val stepText = groups[GROUP_STEP]

            if (flowName == null || index == null || stepText == null) {
                raise(InvalidLineFormatException(line))
            }

            val currentFlow = values.current.firstOrNull()
            val currentStep = currentFlow?.steps?.get(index)
            if (currentFlow == null || currentStep == null) {
                raise(InvalidParserStateException(line))
            }

            currentStep.attemptCount++
        }

    private fun parseStepPassed(
        values: ValueHolder,
        line: TextLine
    ): Either<ReportParsingException, Unit> =
        either {
            val groups = REGEX_STEP_PASSED.aggregateGroups(line.text)

            val flowName = groups[GROUP_FLOW_NAME]
                ?: raise(EmptyParameterException(line))

            val index = groups[GROUP_INDEX]?.toIntSafely()
                ?: raise(EmptyParameterException(line))

            val currentFlow = values.current.firstOrNull()
                ?: raise(InvalidParserStateException(line))

            if (currentFlow.name != flowName) {
                raise(InvalidFlowNameException(flowName))
            }

            val currentStep = currentFlow.steps[index]
                ?: raise(InvalidParserStateException(line))

            currentStep.attemptCount++
            currentStep.isSuccess = true
        }

    private fun parseStepFailed(
        values: ValueHolder,
        line: TextLine
    ): Either<ReportParsingException, Unit> =
        either {
            val groups = REGEX_STEP_FAILED.aggregateGroups(line.text)

            val flowName = groups[GROUP_FLOW_NAME]
                ?: raise(EmptyParameterException(line))

            val index = groups[GROUP_INDEX]?.toIntSafely()
                ?: raise(EmptyParameterException(line))

            val error = groups[GROUP_ERROR]

            val currentFlow = values.current.firstOrNull()
                ?: raise(InvalidParserStateException(line))

            if (currentFlow.name != flowName) {
                raise(InvalidFlowNameException(flowName))
            }

            val currentStep = currentFlow.steps[index]
                ?: raise(InvalidParserStateException(line))

            currentStep.attemptCount++
            currentStep.isSuccess = false
            currentStep.error = error
        }

    private fun parseStackTrace(
        values: ValueHolder,
        queue: Queue<TextLine>,
        startLine: TextLine
    ): Either<ReportParsingException, Unit> =
        either {
            val stacktrace = mutableListOf<TextLine>()
                .apply {
                    add(startLine)
                }

            while (true) {
                val line = queue.peek()

                if (line.text.trim().startsWith("at") ||
                    line.text.contains(EXCEPTION, ignoreCase = true)
                ) {
                    stacktrace.add(queue.poll())
                } else {
                    break
                }

                if (queue.isEmpty()) {
                    break
                }
            }

            val currentFlow = values.finished.values.lastOrNull()
                ?: raise(InvalidParserStateException(startLine))

            currentFlow.stacktrace = stacktrace
                .joinToString(
                    separator = StringUtils.NEW_LINE,
                    transform = { line -> line.text }
                )
        }

    private fun Regex.aggregateGroups(line: String): RegexMatches {
        return RegexMatches(
            groups = findAll(line)
                .toList()
                .firstOrNull()
                ?.groups
        )
    }

    private class RegexMatches(
        private val groups: MatchGroupCollection?
    ) {
        operator fun get(key: String): String? = groups?.get(key)?.value
    }

    private data class ValueHolder(
        val current: Deque<MutableFlow>,
        val finished: LinkedHashMap<String, MutableFlow>
    )

    private data class MutableFlow(
        val name: String,
        var steps: TreeMap<Int, MutableStep> = TreeMap(),
        var isSuccess: Boolean? = null,
        var error: String? = null,
        var stacktrace: String? = null
    )

    private data class MutableStep(
        val index: Int,
        val step: String,
        val innerFlowName: String?,
        var attemptCount: Int,
        var isSuccess: Boolean?,
        var error: String?
    )

    companion object {
        private const val GROUP_FLOW_NAME = "flowName"
        private const val GROUP_INDEX = "index"
        private const val GROUP_STEP = "step"
        private const val GROUP_ERROR = "error"

        private val EXCEPTION = "exception"

        private val REGEX_RUN_FLOW =
            Regex("Run flow '(?<$GROUP_FLOW_NAME>.+?)'$")

        private val REGEX_FLOW_STARTED =
            Regex("Start flow '(?<$GROUP_FLOW_NAME>.+?)'$")

        private val REGEX_FLOW_PASSED =
            Regex("Flow '(?<$GROUP_FLOW_NAME>.+?)' finished successfully$")

        private val REGEX_FLOW_FAILED =
            Regex("Flow '(?<$GROUP_FLOW_NAME>.+?)' failed: (?<$GROUP_ERROR>.+?)$")

        private val REGEX_STEP_PASSED =
            Regex("\\[(?<$GROUP_FLOW_NAME>.+?)] Step (?<$GROUP_INDEX>\\d+?): SUCCESS$")

        private val REGEX_STEP_FAILED =
            Regex(
                "\\[(?<$GROUP_FLOW_NAME>.+?)] Step (?<$GROUP_INDEX>\\d+?): FAILED, (?<$GROUP_ERROR>.+?)$"
            )

        private val REGEX_STEP_STARTED =
            Regex("\\[(?<$GROUP_FLOW_NAME>.+?)] Step (?<$GROUP_INDEX>\\d+?): (?<$GROUP_STEP>.+?)$")

        private val REGEX_STEP_RESTARTED =
            Regex("\\[(?<$GROUP_FLOW_NAME>.+?)] Step (?<$GROUP_INDEX>\\d+?): (?<$GROUP_STEP>.+?)\$")
    }
}