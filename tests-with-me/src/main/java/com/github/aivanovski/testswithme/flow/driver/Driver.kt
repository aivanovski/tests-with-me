package com.github.aivanovski.testswithme.flow.driver

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.KeyCode
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.entity.exception.DriverException

interface Driver<NodeType> {
    fun sendBroadcast(
        packageName: String,
        action: String,
        data: Map<String, String>
    ): Either<DriverException, Unit>

    fun launchApp(packageName: String): Either<DriverException, Unit>

    fun getUiTree(): Either<DriverException, UiNode<NodeType>>

    fun tapOn(uiNode: UiNode<NodeType>): Either<DriverException, Unit>

    fun longTapOn(uiNode: UiNode<NodeType>): Either<DriverException, Unit>

    fun inputText(
        text: String,
        uiNode: UiNode<NodeType>
    ): Either<DriverException, Unit>

    fun pressKey(key: KeyCode): Either<DriverException, Unit>
}