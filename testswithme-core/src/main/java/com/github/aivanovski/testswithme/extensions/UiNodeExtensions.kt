package com.github.aivanovski.testswithme.extensions

import com.github.aivanovski.testswithme.domain.fomatters.UiNodeFormatter
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiEntity
import com.github.aivanovski.testswithme.entity.UiNode
import java.util.LinkedList

fun UiNode<*>.matches(selector: UiElementSelector): Boolean {
    return entity.matches(selector)
}

fun UiNode<*>.toSerializableTree(): UiNode<Unit> {
    return this.map { _ -> Unit }
}

fun <T, Node> UiNode<T>.transformNode(
    onNewNode: (node: UiNode<T>) -> Node,
    onNewChild: (parent: Node, child: Node) -> Unit
): Node {
    val root = this
    val newRoot = onNewNode.invoke(root)

    val queue = LinkedList<Pair<Node, UiNode<T>>>()
    for (node in root.nodes) {
        queue.add(newRoot to node)
    }

    while (queue.isNotEmpty()) {
        val (newParent, node) = queue.removeFirst()

        val newNode = onNewNode.invoke(node)
        onNewChild.invoke(newParent, newNode)

        if (node.nodes.isNotEmpty()) {
            for (child in node.nodes) {
                queue.add(newNode to child)
            }
        }
    }

    return newRoot
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

fun <T> UiNode<T>.format(formatter: UiNodeFormatter): String {
    return formatter.format(this)
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

fun <T> UiNode<T>.cloneTree(): UiNode<T> {
    return this.map { value -> value }
}

fun <T> UiNode<T>.removeEmptyParents(
    isEmptyPredicate: (entity: UiEntity) -> Boolean
): UiNode<T> {
    val root = this.cloneTree()

    val queue = LinkedList<Pair<UiNode<T>?, UiNode<T>>>()
    queue.add(null to root)

    var newRoot = root

    while (queue.isNotEmpty()) {
        repeat(queue.size) {
            val (parent, node) = queue.removeFirst()

            val isNodeEmpty = isEmptyPredicate.invoke(node.entity)
            if (isNodeEmpty
                && (parent == null || parent.nodes.size == 1)
                && node.nodes.size == 1
            ) {
                val child = node.nodes.first()

                if (parent == null) {
                    newRoot = child
                    queue.add(null to newRoot)
                } else {
                    parent.nodes.removeAt(0)
                    parent.nodes.add(child)
                    queue.add(parent to child)
                }
            } else {
                for (child in node.nodes) {
                    queue.add(node to child)
                }
            }
        }
    }

    return newRoot
}

fun <T> UiNode<T>.removeEmptyNodes(
    isEmptyPredicate: (entity: UiEntity) -> Boolean
): UiNode<T> {
    val root = this

    var index = 0
    val newRoot = root.map { value ->
        ValueWrapper(
            key = index++,
            isEmpty = false,
            wrappedValue = value
        )
    }

    fun dfs(node: UiNode<ValueWrapper<T>>) {
        for (child in node.nodes) {
            dfs(child)
        }

        var childIdx = 0
        while (childIdx < node.nodes.size) {
            val child = node.nodes[childIdx]
            if (child.source.isEmpty) {
                node.nodes.removeAt(childIdx)
            } else {
                childIdx++
            }
        }

        node.source.isEmpty = (isEmptyPredicate.invoke(node.entity) && node.nodes.isEmpty())
    }

    dfs(newRoot)

    return newRoot.map { value -> value.wrappedValue }
}

private data class ValueWrapper<T>(
    val key: Int,
    var isEmpty: Boolean,
    val wrappedValue: T
)