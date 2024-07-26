package com.github.aivanovski.testwithme.flow.runner.listener

import arrow.core.Either
import com.github.aivanovski.testwithme.entity.Flow
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.flow.commands.StepCommand

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

    override fun onFlowFinished(flow: Flow, result: Either<FlowExecutionException, Any>) {
        listeners.forEach { listener ->
            listener.onFlowFinished(flow, result)
        }
    }

    override fun onStepStarted(
        flow: Flow,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    ) {
        listeners.forEach { listener ->
            listener.onStepStarted(flow, command, stepIndex, attemptIndex)
        }
    }

    override fun onStepFinished(
        flow: Flow,
        command: StepCommand,
        stepIndex: Int,
        result: Either<FlowExecutionException, Any>
    ) {
        listeners.forEach { listener ->
            listener.onStepFinished(flow, command, stepIndex, result)
        }
    }
}