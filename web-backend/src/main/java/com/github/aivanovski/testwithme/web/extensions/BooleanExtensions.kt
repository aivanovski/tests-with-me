package com.github.aivanovski.testwithme.web.extensions

fun Boolean.asInt(): Int {
    return if (this) 1 else 0
}

fun Int.asBoolean(): Boolean {
    return this == 1
}