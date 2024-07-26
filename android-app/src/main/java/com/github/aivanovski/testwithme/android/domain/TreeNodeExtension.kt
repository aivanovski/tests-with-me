package com.github.aivanovski.testwithme.android.domain

import com.github.aivanovski.testwithme.android.entity.Group
import com.github.aivanovski.testwithme.android.entity.MutableTreeNode
import com.github.aivanovski.testwithme.android.entity.Tree
import com.github.aivanovski.testwithme.android.entity.TreeNode
import java.util.LinkedList

fun List<Group>.buildGroupTree(): Tree {
    val groups = this

    val uidToNodeMap = HashMap<String, MutableTreeNode>()
    for (group in groups) {
        uidToNodeMap[group.uid] = MutableTreeNode(
            entity = group,
            nodes = mutableListOf()
        )
    }

    val rootNodes = mutableListOf<MutableTreeNode>()
    for (group in groups) {
        val groupNode = uidToNodeMap[group.uid] ?: continue
        val children = uidToNodeMap[group.parentUid]
            ?.nodes
            ?: rootNodes

        children.add(groupNode)
    }

    return Tree(nodes = rootNodes)
}

fun Tree.findNodeByUid(uid: String): TreeNode? {
    val queue = LinkedList<TreeNode>()
        .apply {
            addAll(nodes)
        }

    while (queue.isNotEmpty()) {
        val node = queue.pop()

        if (node.entity.uid == uid) {
            return node
        }

        for (child in node.nodes) {
            queue.push(child)
        }
    }

    return null
}

fun TreeNode.getDescendantNodes(): List<TreeNode> {
    val root = this

    val queue = LinkedList<TreeNode>()
        .apply {
            addAll(root.nodes)
        }

    val descendants = mutableListOf<TreeNode>()
    while (queue.isNotEmpty()) {
        repeat(queue.size) {
            val node = queue.removeFirst()

            descendants.add(node)

            for (child in node.nodes) {
                queue.add(child)
            }
        }
    }

    return descendants
}