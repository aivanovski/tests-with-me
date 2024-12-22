package com.github.aivanovski.testswithme.domain.tree

import com.github.aivanovski.testswithme.domain.tree.entity.MutableTreeNode
import com.github.aivanovski.testswithme.domain.tree.entity.TreeNode
import java.util.LinkedList

fun <T, R> TreeNode.map(transform: (T) -> (R)): TreeNode {
    val root = this

    val newRoot = MutableTreeNode(
        uid = root.uid,
        entity = if (root.entity != null) transform.invoke(root.entity as T) else null,
        nodes = mutableListOf()
    )

    val queue = LinkedList<Pair<MutableTreeNode, TreeNode>>()
    for (node in root.nodes) {
        queue.add(newRoot to node)
    }

    while (queue.isNotEmpty()) {
        val (newParent, node) = queue.removeFirst()

        val newNode = MutableTreeNode(
            uid = node.uid,
            entity = if (node.entity != null) transform.invoke(node.entity as T) else null,
            nodes = mutableListOf()
        )
        newParent.nodes.add(newNode)

        for (childNode in node.nodes) {
            queue.add(newNode to childNode)
        }
    }

    return newRoot
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
            val node = queue.removeFirst()

            layer.add(node)

            queue.addAll(node.nodes)
        }

        layers.add(layer)
    }

    return layers
}

fun TreeNode.findNodeByUid(uid: String): TreeNode? {
    val root = this

    val queue = LinkedList<TreeNode>()
        .apply {
            add(root)
        }

    while (queue.isNotEmpty()) {
        val node = queue.pop()

        if (node.uid == uid) {
            return node
        }

        for (childNode in node.nodes) {
            queue.push(childNode)
        }
    }

    return null
}

fun TreeNode.getDescendantNodes(): List<TreeNode> {
    val root = this

    val queue = LinkedList<TreeNode>()
        .apply {
            add(root)
        }

    val descendants = mutableListOf<TreeNode>()
    while (queue.isNotEmpty()) {
        repeat(queue.size) {
            val node = queue.removeFirst()

            if (node.uid != root.uid) {
                descendants.add(node)
            }

            queue.addAll(node.nodes)
        }
    }

    return descendants
}