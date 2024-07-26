package com.github.aivanovski.testwithme.android.extensions

import android.content.Intent
import android.os.Build
import android.os.Parcelable

fun <T : Parcelable> Intent.getParcelableCompat(key: String, type: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= 33) {
        this.getParcelableExtra(key, type)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(key)
    }
}