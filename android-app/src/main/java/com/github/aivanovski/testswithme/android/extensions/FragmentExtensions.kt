package com.github.aivanovski.testswithme.android.extensions

import androidx.fragment.app.Fragment

fun Fragment.requireArgument(key: String): Nothing {
    throw IllegalStateException("Fragment require argument with key: $key")
}