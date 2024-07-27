package com.github.aivanovski.testswithme.extensions

import com.github.aivanovski.testswithme.entity.Bounds

fun Bounds.toShortString(): String {
    return "[$left,$top:$right,$bottom]"
}