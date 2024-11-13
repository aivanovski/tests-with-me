package com.github.aivanovski.testswithme.extensions

import com.github.aivanovski.testswithme.flow.error.DriverError
import com.github.aivanovski.testswithme.flow.error.FlowError

fun FlowError.isFlakyException(): Boolean {
    return this is FlowError.UiNodeError ||
        this is DriverError.FailedToGetUiNodesError ||
        this is FlowError.AssertionError
}