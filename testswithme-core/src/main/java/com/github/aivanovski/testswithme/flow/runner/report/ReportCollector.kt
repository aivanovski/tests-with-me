package com.github.aivanovski.testswithme.flow.runner.report

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.Flow
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testswithme.flow.runner.listener.FlowLifecycleListener
import com.github.aivanovski.testswithme.flow.runner.report.ReportWriter.DefaultNameTransformer

class ReportCollector : FlowLifecycleListener {

    private val lines = mutableListOf<String>()
    private val logger = ReportWriter(
        writer = createOutputWriter(),
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
        step: FlowStep,
        stepIndex: Int,
        attemptIndex: Int
    ) {
        logger.onStepStarted(flow, step, stepIndex, attemptIndex)
    }

    override fun onStepFinished(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        result: Either<FlowExecutionException, Any>
    ) {
        logger.onStepFinished(flow, step, stepIndex, result)
    }

    private fun createOutputWriter(): OutputWriter {
        return object : OutputWriter {
            override fun println(
                level: OutputWriter.Level,
                line: String
            ) {
                lines.add(line)
            }
        }
    }
}