package com.github.aivanovski.testswithme.flow.driver

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.KeyCode
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.flow.error.DriverError

interface Driver<NodeType> {
    fun sendBroadcast(
        packageName: String,
        action: String,
        data: Map<String, String>
    ): Either<DriverError, Unit>

    fun launchApp(packageName: String): Either<DriverError, Unit>

    fun getUiTree(): Either<DriverError, UiNode<NodeType>>

    fun tapOn(uiNode: UiNode<NodeType>): Either<DriverError, Unit>

    fun longTapOn(uiNode: UiNode<NodeType>): Either<DriverError, Unit>

    fun inputText(
        text: String,
        uiNode: UiNode<NodeType>
    ): Either<DriverError, Unit>

    fun pressKey(key: KeyCode): Either<DriverError, Unit>
}