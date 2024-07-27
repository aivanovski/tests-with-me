package com.github.aivanovski.testswithme.android.presentation.core

import java.util.concurrent.ConcurrentHashMap

typealias EventListener<T> = (event: T) -> Unit

class EventProviderImpl<T> : EventProvider<T> {

    private val listenerBySubscriberType: MutableMap<String, EventListener<*>> = ConcurrentHashMap()

    override fun subscribe(
        subscriber: Any,
        listener: (event: T) -> Unit
    ) {
        listenerBySubscriberType[subscriber.key()] = listener
    }

    override fun unsubscribe(subscriber: Any) {
        listenerBySubscriberType.remove(subscriber.key())
    }

    @Suppress("UNCHECKED_CAST")
    override fun sendEvent(event: T) {
        for (listener in listenerBySubscriberType.values) {
            (listener as EventListener<T>).invoke(event)
        }
    }

    override fun isSubscribed(subscriber: Any): Boolean {
        return listenerBySubscriberType.containsKey(subscriber.key())
    }

    override fun clear() {
        listenerBySubscriberType.clear()
    }

    private fun Any.key(): String = this::class.java.name
}