package com.github.aivanovski.testswithme.extensions

import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.utils.StringUtils
import java.lang.StringBuilder
import java.util.LinkedList

private const val DUMP_SEPARATOR = ", "
private const val DUMP_SEPARATOR_INDICATOR = "["

fun UiNode<*>.matches(selector: UiElementSelector): Boolean {
    return entity.matches(selector)
}

fun UiNode<*>.toSerializableTree(): UiNode<Unit> {
    return this.map { _ -> Unit }
}

fun <T, R> UiNode<T>.map(transform: (T) -> R): UiNode<R> {
    val newRoot = UiNode(
        source = transform.invoke(this.source),
        entity = this.entity,
        nodes = mutableListOf()
    )

    val queue = LinkedList<Pair<UiNode<R>, UiNode<T>>>()
    for (node in this.nodes) {
        queue.add(newRoot to node)
    }

    while (queue.isNotEmpty()) {
        val (newParent, node) = queue.removeFirst()

        val newNode = UiNode(
            source = transform.invoke(node.source),
            entity = node.entity,
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

fun <T> UiNode<T>.traverseAndCollect(predicate: (UiNode<T>) -> Boolean): List<UiNode<T>> {
    val result = mutableListOf<UiNode<T>>()

    val nodes = LinkedList<UiNode<T>>()
    nodes.addAll(this.nodes)

    while (nodes.isNotEmpty()) {
        repeat(nodes.size) {
            val node = nodes.removeFirst()

            if (predicate(node)) {
                result.add(node)
            }

            nodes.addAll(node.nodes)
        }
    }

    return result
}

fun <T> UiNode<T>.findNode(predicate: (UiNode<T>) -> Boolean): UiNode<T>? {
    return traverseAndCollect(predicate).firstOrNull()
}

fun <T> UiNode<T>.getNodeParents(target: UiNode<T>): List<UiNode<T>> {
    val result = mutableListOf<UiNode<T>>()

    this.traverseParents(target, result)

    return result.reversed()
}

fun <T> UiNode<T>.findParentNode(
    startNode: UiNode<T>,
    parentSelector: UiElementSelector
): UiNode<T>? {
    val parents = this.getNodeParents(target = startNode)

    return parents.lastOrNull { parent -> parent.matches(parentSelector) }
}

private fun <T> UiNode<T>.traverseParents(
    target: UiNode<T>,
    result: MutableList<UiNode<T>>
): Boolean {
    if (this == target) {
        return true
    }

    for (childNode in nodes) {
        if (childNode.traverseParents(target, result)) {
            result.add(this)
            return true
        }
    }

    return false
}

fun UiNode<*>.hasElement(element: UiElementSelector): Boolean {
    val matchedNodes = this.traverseAndCollect { node -> node.matches(element) }
    return matchedNodes.isNotEmpty()
}

fun <T> UiNode<T>.dumpToString(initialIndent: String = StringUtils.EMPTY): String {
    val lines = mutableListOf<String>()

    visitWithDepth { node, depth ->
        val indent = "  ".repeat(depth)
        lines.add("$initialIndent$indent${node.formatShortDescription()}")
    }

    return lines.joinToString(separator = "\n")
}

fun <T> UiNode<T>.visitWithDepth(visitor: (node: UiNode<T>, depth: Int) -> Unit) {
    val nodes = LinkedList<Pair<Int, UiNode<T>>>()
    nodes.add(0 to this)

    while (nodes.isNotEmpty()) {
        val (depth, node) = nodes.pop()

        visitor.invoke(node, depth)

        for (childNode in node.nodes.reversed()) {
            nodes.push((depth + 1) to childNode)
        }
    }
}

fun UiNode<*>.formatShortDescription(): String {
    val node = this

    return StringBuilder()
        .apply {
            val className = node.entity.className

            val id = node.entity.resourceId
            val text = node.entity.text
            val cd = node.entity.contentDescription
            val bounds = node.entity.bounds?.toShortString()
            val isEditable = node.entity.isEditable
            val isFocused = node.entity.isFocused
            val isClickable = node.entity.isClickable
            val childCount = node.nodes.size

            if (className != null) {
                val lastDot = className.lastIndexOf(".")
                if (lastDot != -1) {
                    append(className.substring(lastDot + 1))
                } else {
                    append(className)
                }
            }

            append("[")

            if (childCount > 0) {
                append("children=$childCount")
            }

            if (id != null) {
                appendWithSeparator(
                    "id=$id",
                    DUMP_SEPARATOR,
                    DUMP_SEPARATOR_INDICATOR
                )
            }

            if (text != null) {
                appendWithSeparator(
                    "text=$text",
                    DUMP_SEPARATOR,
                    DUMP_SEPARATOR_INDICATOR
                )
            }

            if (cd != null) {
                appendWithSeparator(
                    "contDesc=$cd",
                    DUMP_SEPARATOR,
                    DUMP_SEPARATOR_INDICATOR
                )
            }

            if (bounds != null) {
                appendWithSeparator(
                    "bounds=$bounds",
                    DUMP_SEPARATOR,
                    DUMP_SEPARATOR_INDICATOR
                )
            }

            if (isEditable == true) {
                appendWithSeparator(
                    "EDITABLE",
                    DUMP_SEPARATOR,
                    DUMP_SEPARATOR_INDICATOR
                )
            }

            if (isFocused == true) {
                appendWithSeparator(
                    "FOCUSED",
                    DUMP_SEPARATOR,
                    DUMP_SEPARATOR_INDICATOR
                )
            }

            if (isClickable == true) {
                appendWithSeparator(
                    "CLICKABLE",
                    DUMP_SEPARATOR,
                    DUMP_SEPARATOR_INDICATOR
                )
            }

            append("]")
        }
        .toString()
}

private fun StringBuilder.appendWithSeparator(
    value: String,
    separator: String,
    separatorIndicator: String
) {
    if (!this.endsWith(separatorIndicator)) {
        append(separator)
    }
    append(value)
}