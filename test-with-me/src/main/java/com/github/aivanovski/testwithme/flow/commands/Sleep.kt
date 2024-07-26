package com.github.aivanovski.testwithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testwithme.entity.Duration
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.extensions.toMilliseconds
import com.github.aivanovski.testwithme.extensions.toReadableFormat
import com.github.aivanovski.testwithme.flow.runner.ExecutionContext
import kotlinx.coroutines.delay

class Sleep(
    private val duration: Duration
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return "Sleep %s".format(duration.toReadableFormat())
    }

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowExecutionException, Unit> {
        delay(duration.toMilliseconds())
        return Either.Right(Unit)
    }
}