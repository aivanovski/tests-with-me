package com.github.aivanovski.testswithme.web.extensions

fun Boolean.asInt(): Int {
    return if (this) 1 else 0
}

fun Int.asBoolean(): Boolean {
    return this == 1
}