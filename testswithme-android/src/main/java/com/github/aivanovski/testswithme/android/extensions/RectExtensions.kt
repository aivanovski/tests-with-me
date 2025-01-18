package com.github.aivanovski.testswithme.android.extensions

import android.graphics.Rect
import com.github.aivanovski.testswithme.entity.Bounds

fun Rect.toBounds(): Bounds {
    return Bounds(
        left = left,
        right = right,
        top = top,
        bottom = bottom
    )
}