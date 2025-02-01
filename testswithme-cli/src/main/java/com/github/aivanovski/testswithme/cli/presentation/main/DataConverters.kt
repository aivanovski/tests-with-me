package com.github.aivanovski.testswithme.cli.presentation.main

import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.Sha256HashDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.UiBoundsDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.UiEntityDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.UiNodeDto
import com.github.aivanovski.testswithme.entity.Bounds
import com.github.aivanovski.testswithme.entity.Hash
import com.github.aivanovski.testswithme.entity.UiEntity
import com.github.aivanovski.testswithme.entity.UiNode
import java.util.LinkedList

fun Hash.toDto(): Sha256HashDto {
    return Sha256HashDto(value)
}

fun <T> UiNodeDto.toUiNode(transform: (node: UiNodeDto) -> T): UiNode<T> {
    val root = this
    val newRoot = UiNode(
        source = transform.invoke(root),
        entity = root.entity.toEntity(),
        nodes = mutableListOf()
    )

    val queue = LinkedList<Pair<UiNode<T>, UiNodeDto>>()
    for (node in root.nodes) {
        queue.add(newRoot to node)
    }

    while (queue.isNotEmpty()) {
        val (newParent, node) = queue.removeFirst()

        val newNode = UiNode(
            source = transform.invoke(node),
            entity = node.entity.toEntity(),
            nodes = mutableListOf()
        )
        newParent.nodes.add(newNode)

        if (node.nodes.isNotEmpty()) {
            for (child in node.nodes) {
                queue.add(newNode to child)
            }
        }
    }

    return newRoot
}

fun UiEntityDto.toEntity(): UiEntity =
    UiEntity(
        resourceId = null,
        packageName = packageName,
        className = className,
        bounds = bounds?.convert(),
        text = text,
        contentDescription = contentDescription,
        isEnabled = isEnabled,
        isEditable = isEditable,
        isFocused = isFocused,
        isFocusable = isFocusable,
        isClickable = isClickable,
        isLongClickable = isLongClickable,
        isCheckable = isCheckable,
        isChecked = isChecked
    )

fun UiBoundsDto.convert(): Bounds =
    Bounds(left, top, right, bottom)
