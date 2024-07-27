package com.github.aivanovski.testwithme.android.domain.flow.logger

import com.github.aivanovski.testwithme.utils.Logger
import java.lang.Exception
import timber.log.Timber

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