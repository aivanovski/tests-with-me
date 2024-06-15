package com.github.aivanovski.testwithme.android.domain.flow

import arrow.core.Either
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.extensions.unwrapError
import com.github.aivanovski.testwithme.flow.commands.StepCommand
import timber.log.Timber

class TimberFlowReporter : FlowLifecycleListener {

    override fun onFlowStarted(flow: FlowEntry) {
        Timber.d("Start flow '%s'", flow.name)
    }

    override fun onFlowFinished(flow: FlowEntry, result: Either<AppException, Any>) {
        if (result.isRight()) {
            Timber.d("Flow '%s' finished successfully", flow.name)
        } else {
            val exception = result.unwrapError()
            Timber.d(
                "Flow '%s' failed: %s",
                flow.name,
                exception.message ?: exception.javaClass.simpleName
            )
        }
    }

    override fun onStepStarted(
        flow: FlowEntry,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    ) {
        if (attemptIndex == 0) {
            Timber.d("[%s] Step %s: %s", flow.name, stepIndex + 1, command.describe())
        } else {
            Timber.d("[%s] Retry %s: %s", flow.name, stepIndex + 1, command.describe())
        }
    }

    override fun onStepFinished(
        flow: FlowEntry,
        command: StepCommand,
        stepIndex: Int,
        result: Either<AppException, Any>
    ) {
        val resultMessage = if (result.isLeft()) "FAILED" else "SUCCESS"
        Timber.d("[%s] Step %s: %s", flow.name, stepIndex + 1, resultMessage)
    }
}