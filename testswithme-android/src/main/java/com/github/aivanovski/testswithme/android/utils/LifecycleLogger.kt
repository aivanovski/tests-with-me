package com.github.aivanovski.testswithme.android.utils

import com.arkivanov.essenty.lifecycle.Lifecycle
import timber.log.Timber

class LifecycleLogger(
    private val tag: String,
    private val lifecycle: Lifecycle
) : Lifecycle.Callbacks {

    init {
        lifecycle.subscribe(this)
    }

    override fun onCreate() {
        Timber.d("[$tag] onCreate")
    }

    override fun onResume() {
        Timber.d("[$tag] onResume")
    }

    override fun onStart() {
        Timber.d("[$tag] onStart")
    }

    override fun onPause() {
        Timber.d("[$tag] onPause")
    }

    override fun onStop() {
        Timber.d("[$tag] onStop")
    }

    override fun onDestroy() {
        Timber.d("[$tag] onDestroy")
        lifecycle.unsubscribe(this)
    }
}