package com.github.aivanovski.testswithme.android.domain.flow.logger

import com.github.aivanovski.testswithme.flow.runner.report.OutputWriter.Level
import com.github.aivanovski.testswithme.utils.Logger
import timber.log.Timber

class TimberFlowLogger(
    private val tag: String
) : Logger {

    override fun println(
        level: Level,
        line: String
    ) {
        when (level) {
            Level.DEBUG -> Timber.tag(tag).d(line)
            Level.ERROR -> Timber.tag(tag).e(line)
        }
    }
}