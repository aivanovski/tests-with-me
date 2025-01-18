package com.github.aivanovski.testswithme.cli.presentation.core

import java.io.OutputStream
import org.slf4j.Logger

class SystemErrorToLoggerOutputStream(
    private val logger: Logger
) : OutputStream() {

    private val buffer = StringBuilder()

    override fun write(b: Int) {
        if (b.toChar() == '\n') {
            logger.error(buffer.toString())
            buffer.clear()
        } else {
            buffer.append(b.toChar())
        }
    }
}