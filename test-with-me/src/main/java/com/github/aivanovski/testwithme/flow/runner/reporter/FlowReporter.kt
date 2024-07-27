package com.github.aivanovski.testwithme.flow.runner.reporter

import arrow.core.Either
import com.github.aivanovski.testwithme.entity.Flow
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.extensions.ellipsize
import com.github.aivanovski.testwithme.extensions.getRootCause
import com.github.aivanovski.testwithme.extensions.unwrapError
import com.github.aivanovski.testwithme.flow.commands.StepCommand
import com.github.aivanovski.testwithme.flow.runner.listener.FlowLifecycleListener
import com.github.aivanovski.testwithme.utils.Logger
import com.github.aivanovski.testwithme.utils.StringUtils

open class FlowReporter(
    private val logger: Logger,
    private val flowTransformer: FlowTransformer = DefaultNameTransformer()
) : FlowLifecycleListener {

    override fun onFlowStarted(flow: Flow) {
        logger.debug("Start flow '%s'".format(flow.name))
    }

    override fun onFlowFinished(
        flow: Flow,
        result: Either<FlowExecutionException, Any>
    ) {
        if (result.isRight()) {
            logger.debug("Flow '%s' finished successfully".format(flowTransformer.transform(flow)))
        } else {
            val exception = result.unwrapError()
            val cause = exception.getRootCause()
            val message = cause.message ?: cause.javaClass.simpleName

            logger.error(
                "Flow '%s' failed: %s".format(
                    flowTransformer.transform(flow),
                    message
                )
            )

            logger.printStackTrace(exception)
        }
    }

    override fun onStepStarted(
        flow: Flow,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    ) {
        if (attemptIndex == 0) {
            logger.debug(
                "[%s] Step %s: %s".format(
                    flowTransformer.transform(flow),
                    stepIndex + 1,
                    command.describe()
                )
            )
        } else {
            logger.debug(
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
            logger.debug(
                "[%s] Step %s: SUCCESS".format(
                    flowTransformer.transform(flow),
                    stepIndex + 1
                )
            )
        } else {
            val cause = result.unwrapError()
            val message = cause.message ?: cause.javaClass.simpleName

            logger.error(
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