package com.github.aivanovski.testswithme.flow.runner.listener

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.Flow
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testswithme.flow.commands.StepCommand

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