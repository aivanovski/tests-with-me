package com.github.aivanovski.testswithme.android.domain.testServer.parser

import android.os.Bundle
import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.domain.testServer.model.TestServerCommand
import com.github.aivanovski.testswithme.android.entity.exception.ParsingException

class TestServerCommandParser {

    fun parse(data: Bundle?): Either<ParsingException, TestServerCommand> =
        either {
            when (val command = data?.getString(COMMAND)) {
                START -> TestServerCommand.Start
                STOP -> TestServerCommand.Stop
                else -> raise(ParsingException("Failed to parse command: $command"))
            }
        }

    companion object {
        private const val COMMAND = "command"

        private const val START = "start"
        private const val STOP = "stop"
    }
}