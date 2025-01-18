package com.github.aivanovski.testswithme.flow.error

import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.toReadableFormat
import kotlinx.serialization.Serializable

@Serializable
sealed class FlowError(
    val cause: String
) {
    @Serializable
    data class AssertionError(
        val message: String,
        val uiRoot: UiNode<Unit>
    ) : FlowError(message)

    @Serializable
    data class UiNodeError(
        val message: String
    ) : FlowError(message)

    @Serializable
    data class FailedToFindUiNodeError(
        val uiNodeSelector: UiElementSelector,
        val uiRoot: UiNode<Unit>?
    ) : FlowError(
        cause = "Failed to find node: %s".format(uiNodeSelector.toReadableFormat())
    )
}

@Serializable
sealed class DriverError(
    val message: String
) : FlowError(message) {

    @Serializable
    object FailedToGetUiNodesError : DriverError(
        message = "Failed to get UI nodes"
    )

    @Serializable
    data class FailedToFindActivityError(
        val packageName: String
    ) : DriverError(
        message = "Unable to find activity for package: $packageName"
    )

    @Serializable
    data class FailedToPerformActionError(
        val actionName: String
    ) : DriverError(
        message = "Unable to perform action: $actionName"
    )

    @Serializable
    data class InvalidKeyPressedError(
        val key: String
    ) : DriverError(
        message = "Unable to determine action for key: $key"
    )
}