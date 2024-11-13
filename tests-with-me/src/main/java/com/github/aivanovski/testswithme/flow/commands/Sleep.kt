package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.Duration
import com.github.aivanovski.testswithme.extensions.toMilliseconds
import com.github.aivanovski.testswithme.extensions.toReadableFormat
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext
import kotlinx.coroutines.delay

class Sleep(
    private val duration: Duration
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return "Sleep %s".format(duration.toReadableFormat())
    }

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, Unit> {
        delay(duration.toMilliseconds())
        return Either.Right(Unit)
    }
}