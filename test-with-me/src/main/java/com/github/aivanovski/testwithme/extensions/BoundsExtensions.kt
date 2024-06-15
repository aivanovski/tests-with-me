package com.github.aivanovski.testwithme.extensions

import com.github.aivanovski.testwithme.entity.Bounds

fun Bounds.toShortString(): String {
    return "[$left,$top:$right,$bottom]"
}