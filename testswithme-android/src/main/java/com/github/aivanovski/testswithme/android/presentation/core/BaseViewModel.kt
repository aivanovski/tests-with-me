package com.github.aivanovski.testswithme.android.presentation.core

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.cancel

abstract class BaseViewModel : ViewModel() {

    protected val intentProvider: CellIntentProvider = CellIntentProviderImpl()
    private val isStarted = AtomicBoolean(false)
    private val blocks = mutableListOf<() -> Unit>()

    @CallSuper
    open fun start() {
        if (isStarted.compareAndSet(false, true)) {
            for (block in blocks) {
                block.invoke()
            }
            blocks.clear()
        }

        if (!intentProvider.isSubscribed(this)) {
            intentProvider.subscribe(this) { intent ->
                handleCellIntent(intent)
            }
        }
    }

    @CallSuper
    open fun destroy() {
        isStarted.set(false)
        intentProvider.clear()
        viewModelScope.cancel()
    }

    fun doOnceWhenStarted(block: () -> Unit) {
        if (isStarted.get()) {
            block.invoke()
        } else {
            blocks.add(block)
        }
    }

    protected open fun handleCellIntent(intent: BaseCellIntent) {
    }
}