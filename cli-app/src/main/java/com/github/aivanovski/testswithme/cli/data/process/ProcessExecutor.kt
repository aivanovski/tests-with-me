package com.github.aivanovski.testswithme.cli.data.process

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.utils.StringUtils.SPACE
import org.buildobjects.process.ProcBuilder

class ProcessExecutor {

    fun run(
        command: String
    ): Either<Exception, String> =
        either {
            val (com, args) = command.splitIntoCommandAndArgs()

            executeInternal(
                input = null,
                command = com,
                arguments = args
            ).bind()
        }

    private fun executeInternal(
        input: ByteArray?,
        command: String,
        arguments: List<String>
    ): Either<Exception, String> =
        either {
            try {
                val builder = ProcBuilder(command, *arguments.toTypedArray())

                if (input != null) {
                    builder.withInput(input)
                }

                builder.run().outputString
            } catch (exception: Exception) {
                raise(exception)
            }
        }

    private fun String.splitIntoCommandAndArgs(): Pair<String, List<String>> {
        if (this.isBlank()) {
            return Pair(this, emptyList())
        }

        if (!this.contains(SPACE)) {
            return Pair(this, emptyList())
        }

        val values = this.split(SPACE)

        return when {
            values.size == 1 -> Pair(values.first(), emptyList())
            values.size > 1 -> Pair(values.first(), values.subList(1, values.size))
            else -> Pair(this, emptyList())
        }
    }
}