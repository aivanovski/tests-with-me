package com.github.aivanovski.testwithme.android.presentation.core

import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellIntent
import java.util.concurrent.ConcurrentHashMap

typealias Listener = (intent: BaseCellIntent) -> Unit

class CellIntentProviderImpl : CellIntentProvider {

    private val listenerBySubscriberType: MutableMap<String, Listener> = ConcurrentHashMap()

    override fun subscribe(
        subscriber: Any,
        listener: (intent: BaseCellIntent) -> Unit
    ) {
        listenerBySubscriberType[subscriber.key()] = listener
    }

    override fun unsubscribe(subscriber: Any) {
        listenerBySubscriberType.remove(subscriber.key())
    }

    override fun sendIntent(intent: BaseCellIntent) {
        for (listener in listenerBySubscriberType.values) {
            listener.invoke(intent)
        }
    }

    override fun isSubscribed(subscriber: Any): Boolean {
        return listenerBySubscriberType.containsKey(subscriber.key())
    }

    override fun clear() {
        listenerBySubscriberType.clear()
    }

    private fun Any.key(): String {
        return this::class.java.name
    }
}