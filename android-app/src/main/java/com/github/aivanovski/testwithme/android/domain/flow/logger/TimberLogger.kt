package com.github.aivanovski.testwithme.android.domain.flow.logger

import com.github.aivanovski.testwithme.utils.Logger
import timber.log.Timber
import java.lang.Exception

class TimberLogger(
    private val tag: String
) : Logger {
    override fun debug(message: String) {
        Timber.tag(tag).d(message)
    }

    override fun error(message: String) {
        Timber.tag(tag).e(message)
    }

    override fun printStackTrace(exception: Exception) {
        Timber.tag(tag).e(exception)
    }
}