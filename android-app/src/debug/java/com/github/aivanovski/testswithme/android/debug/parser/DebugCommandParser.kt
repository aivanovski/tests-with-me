package com.github.aivanovski.testswithme.android.debug.parser

import android.os.Bundle
import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.debug.model.DebugCommand
import com.github.aivanovski.testswithme.android.entity.exception.ParsingException
import com.github.aivanovski.testswithme.android.utils.toBooleanSafely

class DebugCommandParser {

    fun parse(data: Bundle?): Either<ParsingException, DebugCommand> =
        either {
            val isGetUitTree = data?.getString(IS_PRINT_UI_TREE)?.toBooleanSafely()
                ?: raise(ParsingException("No arguments were specified"))

            DebugCommand(
                isPrintUiTree = isGetUitTree
            )
        }

    companion object {
        private const val IS_PRINT_UI_TREE = "isPrintUiTree"
    }
}