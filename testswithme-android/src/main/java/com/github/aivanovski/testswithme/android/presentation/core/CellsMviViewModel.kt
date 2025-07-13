package com.github.aivanovski.testswithme.android.presentation.core

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

abstract class CellsMviViewModel<State, Intent>(
    initialState: State,
    initialIntent: Intent
) : MviViewModel<State, Intent>(
    initialState = initialState,
    initialIntent = initialIntent
) {

    protected val intentProvider: CellIntentProvider = CellIntentProviderImpl()

    abstract fun handleCellIntent(intent: BaseCellIntent)

    override fun start() {
        super.start()

        if (!intentProvider.isSubscribed(this)) {
            intentProvider.subscribe(this) { intent ->
                handleCellIntent(intent)
            }
        }
    }

    override fun destroy() {
        super.destroy()
        intentProvider.clear()
    }
}