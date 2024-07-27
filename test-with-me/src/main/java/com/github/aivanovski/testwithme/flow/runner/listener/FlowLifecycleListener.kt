package com.github.aivanovski.testwithme.flow.runner.listener

import arrow.core.Either
import com.github.aivanovski.testwithme.entity.Flow
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.flow.commands.StepCommand

interface FlowLifecycleListener {

    fun onFlowStarted(flow: Flow)

    fun onFlowFinished(
        flow: Flow,
        result: Either<FlowExecutionException, Any>
    )

    fun onStepStarted(
        flow: Flow,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    )

    fun onStepFinished(
        flow: Flow,
        command: StepCommand,
        stepIndex: Int,
        result: Either<FlowExecutionException, Any>
    )
}