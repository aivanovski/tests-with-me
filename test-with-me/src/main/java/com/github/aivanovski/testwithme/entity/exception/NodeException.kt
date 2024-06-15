package com.github.aivanovski.testwithme.entity.exception

import com.github.aivanovski.testwithme.entity.UiElementSelector
import com.github.aivanovski.testwithme.extensions.toReadableFormat

open class NodeException(
    message: String
) : FlowExecutionException(message)

class FailedToFindNodeException(
    selector: UiElementSelector
) : NodeException(
    message = "Failed to find node: %s".format(selector.toReadableFormat())
)