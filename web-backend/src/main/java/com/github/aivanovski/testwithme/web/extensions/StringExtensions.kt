package com.github.aivanovski.testwithme.web.extensions

import java.util.Base64

fun String.encodeToBase64(): String {
    return Base64.getEncoder().encodeToString(this.toByteArray())
}
