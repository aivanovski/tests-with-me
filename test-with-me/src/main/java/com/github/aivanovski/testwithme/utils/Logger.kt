package com.github.aivanovski.testwithme.utils

import java.lang.Exception

interface Logger {
    fun debug(message: String)
    fun error(message: String)
    fun printStackTrace(exception: Exception)
}