package com.github.aivanovski.testswithme.flow.runner.reporter

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.Flow
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testswithme.extensions.printStackTraceToString
import com.github.aivanovski.testswithme.flow.commands.StepCommand
import com.github.aivanovski.testswithme.flow.runner.listener.FlowLifecycleListener
import com.github.aivanovski.testswithme.flow.runner.reporter.FlowReporter.DefaultNameTransformer
import com.github.aivanovski.testswithme.utils.Logger
import com.github.aivanovski.testswithme.utils.StringUtils.NEW_LINE

class ReportCollector : FlowLifecycleListener {

    private val lines = mutableListOf<String>()
    private val logger = FlowReporter(
        logger = createLogger(),
        flowTransformer = DefaultNameTransformer()
    )

    fun getLines(): List<String> = lines

    fun clear() {
        lines.clear()
    }

    override fun onFlowStarted(flow: Flow) {
        logger.onFlowStarted(flow)
    }

    override fun onFlowFinished(
        flow: Flow,
        result: Either<FlowExecutionException, Any>
    ) {
        logger.onFlowFinished(flow, result)
    }

    override fun onStepStarted(
        flow: Flow,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    ) {
        logger.onStepStarted(flow, command, stepIndex, attemptIndex)
    }

    override fun onStepFinished(
        flow: Flow,
        command: StepCommand,
        stepIndex: Int,
        result: Either<FlowExecutionException, Any>
    ) {
        logger.onStepFinished(flow, command, stepIndex, result)
    }

    private fun createLogger(): Logger {
        return object : Logger {
            override fun debug(message: String) {
                lines.add(message)
            }

            override fun error(message: String) {
                lines.add(message)
            }

            override fun printStackTrace(exception: Exception) {
                val content = exception.printStackTraceToString()
                    .split(NEW_LINE)
                    .filter { line -> line.isNotBlank() }

                lines.addAll(content)
            }
        }
    }
}