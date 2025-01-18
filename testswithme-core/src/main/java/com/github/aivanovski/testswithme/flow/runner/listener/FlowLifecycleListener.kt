package com.github.aivanovski.testswithme.flow.runner.listener

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.Flow
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException

interface FlowLifecycleListener {

    fun onFlowStarted(flow: Flow)

    fun onFlowFinished(
        flow: Flow,
        result: Either<FlowExecutionException, Any>
    )

    fun onStepStarted(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        attemptIndex: Int
    )

    fun onStepFinished(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        result: Either<FlowExecutionException, Any>
    )
}