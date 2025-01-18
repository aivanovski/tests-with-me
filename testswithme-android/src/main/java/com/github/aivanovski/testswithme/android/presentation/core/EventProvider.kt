package com.github.aivanovski.testswithme.android.presentation.core

interface EventProvider<T> {

    fun subscribe(
        subscriber: Any,
        listener: (event: T) -> Unit
    )

    fun unsubscribe(subscriber: Any)
    fun sendEvent(event: T)
    fun isSubscribed(subscriber: Any): Boolean
    fun clear()
}