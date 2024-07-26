package com.github.aivanovski.testwithme.android.utils

fun String.toBooleanSafely(): Boolean? {
    return when {
        this.equals("true", ignoreCase = true) -> true
        this.equals("false", ignoreCase = true) -> false
        else -> null
    }
}