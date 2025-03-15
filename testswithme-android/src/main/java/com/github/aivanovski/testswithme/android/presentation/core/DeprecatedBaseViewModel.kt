package com.github.aivanovski.testswithme.android.presentation.core

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.cancel

// TODO: remove class
@Deprecated("Should be removed")
abstract class DeprecatedBaseViewModel : ViewModel(), ScreenViewModel {

    private val isStarted = AtomicBoolean(false)
    private val blocks = mutableListOf<() -> Unit>()

    @CallSuper
    override fun start() {
        if (isStarted.compareAndSet(false, true)) {
            for (block in blocks) {
                block.invoke()
            }
            blocks.clear()
        }
    }

    @CallSuper
    override fun destroy() {
        isStarted.set(false)
        viewModelScope.cancel()
    }

    fun doOnceWhenStarted(block: () -> Unit) {
        if (isStarted.get()) {
            block.invoke()
        } else {
            blocks.add(block)
        }
    }
}