package com.github.aivanovski.testwithme.android.domain.flow

import arrow.core.Either
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.flow.commands.StepCommand

interface FlowLifecycleListener {

    fun onFlowStarted(
        flow: FlowEntry
    )

    fun onFlowFinished(
        flow: FlowEntry,
        result: Either<AppException, Any>
    )

    fun onStepStarted(
        flow: FlowEntry,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    )

    fun onStepFinished(
        flow: FlowEntry,
        command: StepCommand,
        stepIndex: Int,
        result: Either<AppException, Any>
    )
}