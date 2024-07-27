package com.github.aivanovski.testswithme.web.api.common

import java.text.SimpleDateFormat
import java.util.Locale

object ApiDateFormat {
    val DATE_TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
}