package com.github.aivanovski.testswithme.web.domain.trees

import com.github.aivanovski.testswithme.web.domain.trees.model.TreeNode
import com.github.aivanovski.testswithme.web.entity.Uid
import java.util.LinkedList

fun TreeNode.toUidTree(): List<List<Uid>> =
    this.mapLayers { node -> node.entityUid }

fun <T> TreeNode.mapLayers(transform: (TreeNode) -> T): List<List<T>> {
    val layers = this.treeLayers()
    val result = mutableListOf<List<T>>()

    for (layer in layers) {
        val transformedLayer = layer.map { node -> transform.invoke(node) }
        result.add(transformedLayer)
    }

    return result
}

fun TreeNode.treeLayers(): List<List<TreeNode>> {
    val layers = mutableListOf<List<TreeNode>>()

    val root = this
    val queue = LinkedList<TreeNode>()
        .apply {
            add(root)
        }

    while (queue.isNotEmpty()) {
        val layer = mutableListOf<TreeNode>()

        repeat(queue.size) {
            val node = queue.poll()

            layer.add(node)

            queue.addAll(node.nodes)
        }

        layers.add(layer)
    }

    return layers
}

fun TreeNode.traverse(): List<TreeNode> {
    val queue = LinkedList<TreeNode>()
    queue.add(this)

    val result = mutableListOf<TreeNode>()
    while (queue.isNotEmpty()) {
        repeat(queue.size) {
            val node = queue.poll()

            result.add(node)

            for (child in node.nodes) {
                queue.add(child)
            }
        }
    }

    return result
}

fun TreeNode.findNodeByUid(uid: Uid): TreeNode? {
    val queue = LinkedList<TreeNode>()
        .apply {
            addAll(nodes)
        }

    while (queue.isNotEmpty()) {
        val node = queue.pop()

        if (node.entityUid == uid) {
            return node
        }

        for (child in node.nodes) {
            queue.push(child)
        }
    }

    return null
}