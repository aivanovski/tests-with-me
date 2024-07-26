package com.github.aivanovski.testwithme.android.presentation.core

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellIntent
import kotlinx.coroutines.cancel

abstract class BaseViewModel : ViewModel() {

    protected val intentProvider: CellIntentProvider = CellIntentProviderImpl()

    @CallSuper
    open fun start() {
        if (!intentProvider.isSubscribed(this)) {
            intentProvider.subscribe(this) { intent ->
                handleCellIntent(intent)
            }
        }
    }

    @CallSuper
    open fun destroy() {
        intentProvider.clear()
        viewModelScope.cancel()
    }

    protected open fun handleCellIntent(intent: BaseCellIntent) {
    }
}