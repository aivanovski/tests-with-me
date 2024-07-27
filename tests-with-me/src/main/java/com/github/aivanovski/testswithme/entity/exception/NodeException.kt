package com.github.aivanovski.testswithme.entity.exception

import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.extensions.toReadableFormat

open class NodeException(
    message: String
) : FlowExecutionException(message)

class FailedToFindNodeException(
    selector: UiElementSelector
) : NodeException(
    message = "Failed to find node: %s".format(selector.toReadableFormat())
)