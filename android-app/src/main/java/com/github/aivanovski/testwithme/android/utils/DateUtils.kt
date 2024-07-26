package com.github.aivanovski.testwithme.android.utils

import java.text.DateFormat

object DateUtils {

    fun parseOrNull(format: DateFormat, date: String): Long? {
        return try {
            format.parse(date)?.time
        } catch (exception: Exception) {
            null
        }
    }
}