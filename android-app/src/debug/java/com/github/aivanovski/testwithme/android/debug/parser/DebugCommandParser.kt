package com.github.aivanovski.testwithme.android.debug.parser

import android.os.Bundle
import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.debug.model.DebugCommand
import com.github.aivanovski.testwithme.android.entity.exception.ParsingException
import com.github.aivanovski.testwithme.android.utils.toBooleanSafely

class DebugCommandParser {

    fun parse(
        data: Bundle?
    ): Either<ParsingException, DebugCommand> = either {
        val testFlowData = data?.getString(EXTRA_TEST_FLOW_CONTENT)
        val isGetUitTree = data?.getString(IS_PRINT_UI_TREE)?.toBooleanSafely()

        if (testFlowData == null && isGetUitTree == null) {
            raise(ParsingException("No arguments were specified"))
        }

        DebugCommand(
            testFlowContent = testFlowData,
            isPrintUiTree = isGetUitTree
        )
    }

    companion object {
        private const val EXTRA_TEST_FLOW_CONTENT = "testFlowContent"
        private const val IS_PRINT_UI_TREE = "isPrintUiTree"
    }
}