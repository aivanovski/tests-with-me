package com.github.aivanovski.testwithme.android.extensions

import android.graphics.Rect
import com.github.aivanovski.testwithme.entity.Bounds

fun Rect.toBounds(): Bounds {
    return Bounds(
        left = left,
        right = right,
        top = top,
        bottom = bottom
    )
}