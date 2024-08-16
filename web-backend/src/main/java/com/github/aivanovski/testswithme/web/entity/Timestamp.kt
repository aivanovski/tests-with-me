package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.api.common.ApiDateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Timestamp(
    val milliseconds: Long
) {

    fun formatForTransport(): String {
        return API_FORMAT.format(Date(milliseconds))
    }

    override fun toString(): String {
        return ISO_FORMAT.format(Date(milliseconds))
    }

    companion object {
        private val ISO_FORMAT = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            Locale.ENGLISH
        )

        private val API_FORMAT = SimpleDateFormat(
            ApiDateFormat.DATE_TIME_FORMAT,
            Locale.ENGLISH
        )

        fun fromString(text: String): Timestamp {
            val time = ISO_FORMAT.parse(text)?.time ?: throw IllegalArgumentException()
            return Timestamp(time)
        }

        fun now(): Timestamp {
            return Timestamp(System.currentTimeMillis())
        }
    }
}