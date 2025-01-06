package com.github.aivanovski.testswithme.flow.runner.reporter

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.Flow
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testswithme.extensions.ellipsize
import com.github.aivanovski.testswithme.extensions.getRootCause
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.flow.commands.StepCommand
import com.github.aivanovski.testswithme.flow.runner.listener.FlowLifecycleListener
import com.github.aivanovski.testswithme.utils.StringUtils

class ReportWriter(
    writer: OutputWriter,
    private val flowTransformer: FlowTransformer = DefaultNameTransformer()
) : FlowLifecycleListener {

    private val output = LeveledOutputWriter(writer)

    override fun onFlowStarted(flow: Flow) {
        output.debug("Start flow '%s'".format(flow.name))
    }

    override fun onFlowFinished(
        flow: Flow,
        result: Either<FlowExecutionException, Any>
    ) {
        if (result.isRight()) {
            output.debug("Flow '%s' finished successfully".format(flowTransformer.transform(flow)))
        } else {
            val exception = result.unwrapError()
            val cause = exception.getRootCause()
            val message = cause.message ?: cause.javaClass.simpleName

            output.error(
                "Flow '%s' failed: %s".format(
                    flowTransformer.transform(flow),
                    message
                )
            )

            val stacktrace = exception.stackTraceToString()
            output.error(stacktrace)
        }
    }

    override fun onStepStarted(
        flow: Flow,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    ) {
        if (attemptIndex == 0) {
            output.debug(
                "[%s] Step %s: %s".format(
                    flowTransformer.transform(flow),
                    stepIndex + 1,
                    command.describe()
                )
            )
        } else {
            output.debug(
                "[%s] Retry %s: %s".format(
                    flowTransformer.transform(flow),
                    stepIndex + 1,
                    command.describe()
                )
            )
        }
    }

    override fun onStepFinished(
        flow: Flow,
        command: StepCommand,
        stepIndex: Int,
        result: Either<FlowExecutionException, Any>
    ) {
        if (result.isRight()) {
            output.debug(
                "[%s] Step %s: SUCCESS".format(
                    flowTransformer.transform(flow),
                    stepIndex + 1
                )
            )
        } else {
            val exception = result.unwrapError()
            val message = exception.message ?: exception.javaClass.simpleName

            output.error(
                "[%s] Step %s: FAILED, %s".format(
                    flowTransformer.transform(flow),
                    stepIndex + 1,
                    message
                )
            )
        }
    }

    fun interface FlowTransformer {
        fun transform(flow: Flow): String
    }

    class DefaultNameTransformer : FlowTransformer {
        override fun transform(flow: Flow): String = flow.name
    }

    class ShortNameTransformer : FlowTransformer {
        override fun transform(flow: Flow): String {
            return flow.name.ellipsize(20, StringUtils.DOTS)
        }
    }
}