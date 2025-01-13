package com.github.aivanovski.testswithme.flow.runner.reporter

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.Flow
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testswithme.flow.runner.listener.FlowLifecycleListener
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class TimeCollector : FlowLifecycleListener {

    private val start = AtomicLong(-1L)
    private val end = AtomicLong(-1L)

    override fun onFlowStarted(flow: Flow) {
        if (start.get() == -1L) {
            start.set(System.currentTimeMillis())
        }
    }

    override fun onFlowFinished(
        flow: Flow,
        result: Either<FlowExecutionException, Any>
    ) {
        end.set(System.currentTimeMillis())
    }

    override fun onStepStarted(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        attemptIndex: Int
    ) {
    }

    override fun onStepFinished(
        flow: Flow,
        step: FlowStep,
        stepIndex: Int,
        result: Either<FlowExecutionException, Any>
    ) {
    }

    fun clear() {
        start.set(-1L)
        end.set(-1L)
    }

    fun getDuration(): Duration? {
        val start = start.get()
        val end = end.get()
        val duration = end - start

        if (start == -1L || end == -1L || duration <= 0) {
            return null
        }

        return duration.milliseconds
    }
}