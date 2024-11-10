package com.github.aivanovski.testswithme.cli.domain.printer

import java.util.concurrent.atomic.AtomicReference

enum class OutputLevel(val value: Int) {
    OUTPUT(0),
    DEBUG(1);

    companion object {

        private val currentLevel = AtomicReference(OUTPUT)

        fun isDebug(): Boolean = (getOutputLevel() == DEBUG)

        fun getOutputLevel(): OutputLevel = currentLevel.get()

        fun setOutputLevel(level: OutputLevel) = currentLevel.set(level)
    }
}