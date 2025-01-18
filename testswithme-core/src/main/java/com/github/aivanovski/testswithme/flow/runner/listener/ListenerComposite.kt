package com.github.aivanovski.testswithme.flow.runner.listener

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.Flow
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException

class ListenerComposite : FlowLifecycleListener {

    private val listeners = mutableListOf<FlowLifecycleListener>()

    fun add(listener: FlowLifecycleListener) {
        if (listener !in listeners) {
            listeners.add(listener)
        }
    }

    override fun onFlowStarted(flow: Flow) {
        listeners.forEach { listener ->
            listener.onFlowStarted(flow)
        }
    }

    override fun onFlowFinished(
        flow: Flow,
        result: Either<FlowExecutionException, Any>
    ) {
        listeners.forEach { listener ->
            listener.onFlowFinished(flow, result)
        }
    }

    override fun onStepStarted(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        attemptIndex: Int
    ) {
        listeners.forEach { listener ->
            listener.onStepStarted(flow, step, stepIndex, attemptIndex)
        }
    }

    override fun onStepFinished(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        result: Either<FlowExecutionException, Any>
    ) {
        listeners.forEach { listener ->
            listener.onStepFinished(flow, step, stepIndex, result)
        }
    }
}