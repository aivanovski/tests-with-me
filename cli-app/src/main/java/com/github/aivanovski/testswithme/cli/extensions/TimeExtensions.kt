package com.github.aivanovski.testswithme.cli.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss")

fun Instant.toReadableString(): String {
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = this.atZone(zoneId)
    return zonedDateTime.format(TIME_FORMATTER)
}