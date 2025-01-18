package com.github.aivanovski.testswithme.android.presentation.core

import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent

interface CellIntentProvider {

    fun subscribe(
        subscriber: Any,
        listener: (intent: BaseCellIntent) -> Unit
    )

    fun unsubscribe(subscriber: Any)
    fun sendIntent(intent: BaseCellIntent)
    fun isSubscribed(subscriber: Any): Boolean
    fun clear()
}