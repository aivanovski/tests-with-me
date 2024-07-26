package com.github.aivanovski.testwithme.android.utils

import com.github.aivanovski.testwithme.android.R
import com.github.aivanovski.testwithme.android.domain.resources.ResourceProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val ONE_MINUTE = TimeUnit.MINUTES.toMillis(1)
private val ONE_HOUR = TimeUnit.HOURS.toMillis(1)
private val ONE_DAY = TimeUnit.DAYS.toMillis(1)
private val ONE_WEEK = TimeUnit.DAYS.toMillis(7)
private val DATE_FORMAT = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

fun Long.formatRunTime(resourceProvider: ResourceProvider): String {
    val timeSinceRun = System.currentTimeMillis() - this

    return when {
        timeSinceRun < ONE_MINUTE -> {
            resourceProvider.getString(R.string.moments_ago)
        }

        timeSinceRun < ONE_HOUR -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timeSinceRun)
            resourceProvider.getString(R.string.time_ago, minutes, "minutes")
        }

        timeSinceRun < ONE_DAY -> {
            val hours = TimeUnit.MILLISECONDS.toHours(timeSinceRun)
            resourceProvider.getString(R.string.time_ago, hours, "hours")
        }

        timeSinceRun < ONE_WEEK -> {
            val days = TimeUnit.MILLISECONDS.toDays(timeSinceRun)
            resourceProvider.getString(R.string.time_ago, days, "days")
        }

        else -> DATE_FORMAT.format(Date(this))
    }
}