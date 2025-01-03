package com.github.aivanovski.testswithme.web.data.arguments

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.entity.ApplicationArguments
import com.github.aivanovski.testswithme.web.entity.CliArgument
import com.github.aivanovski.testswithme.web.entity.CliArgument.PROTOCOL
import com.github.aivanovski.testswithme.web.entity.NetworkProtocolType
import com.github.aivanovski.testswithme.web.entity.exception.ParsingException
import java.util.LinkedList

class ArgumentParser {

    fun parse(args: Array<String>): Either<ParsingException, ApplicationArguments> =
        either {
            val queue = LinkedList(args.toList())

            var protocolType = NetworkProtocolType.default()
            while (queue.isNotEmpty()) {
                val name = queue.removeFirst()
                val argumentType = ARGUMENT_NAME_TO_TYPE_MAP[name]

                when (argumentType) {
                    PROTOCOL -> {
                        val value = queue.removeFirstOrNull().orEmpty()
                        protocolType = NetworkProtocolType.getByName(value)
                            ?: raise(
                                ParsingException(
                                    "Unable to parse ${PROTOCOL.longName} argument: $value"
                                )
                            )
                    }

                    else -> raise(ParsingException("Invalid argument name: $name"))
                }
            }

            ApplicationArguments(
                protocolType = protocolType
            )
        }

    companion object {
        private val ARGUMENT_NAME_TO_TYPE_MAP = CliArgument.entries
            .flatMap { argument ->
                listOf(
                    argument.shortName to argument,
                    argument.longName to argument
                )
            }
            .toMap()
    }
}