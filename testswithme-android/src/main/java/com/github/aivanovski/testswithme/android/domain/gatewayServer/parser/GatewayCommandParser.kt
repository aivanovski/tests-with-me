package com.github.aivanovski.testswithme.android.domain.gatewayServer.parser

import android.os.Bundle
import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.domain.gatewayServer.model.GatewayCommand
import com.github.aivanovski.testswithme.android.entity.exception.ParsingException

class GatewayCommandParser {

    fun parse(data: Bundle?): Either<ParsingException, GatewayCommand> =
        either {
            when (val command = data?.getString(COMMAND)) {
                START -> GatewayCommand.Start
                STOP -> GatewayCommand.Stop
                else -> raise(ParsingException("Failed to parse command: $command"))
            }
        }

    companion object {
        private const val COMMAND = "command"

        private const val START = "start"
        private const val STOP = "stop"
    }
}