package com.github.aivanovski.testswithme.utils

import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.MutableStateFlow

class MutableStateFlowDelegate<T>(
    private val stateFlow: MutableStateFlow<T>
) {

    operator fun getValue(
        reference: Any?,
        property: KProperty<*>
    ): T {
        return stateFlow.value
    }

    operator fun setValue(
        reference: Any?,
        property: KProperty<*>,
        value: T
    ) {
        stateFlow.value = value
    }
}

fun <T> mutableStateFlow(initialValue: T): MutableStateFlowDelegate<T> {
    return MutableStateFlowDelegate(MutableStateFlow(initialValue))
}