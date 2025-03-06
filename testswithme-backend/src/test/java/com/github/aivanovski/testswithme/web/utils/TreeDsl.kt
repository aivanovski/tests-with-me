package com.github.aivanovski.testswithme.web.utils

import com.github.aivanovski.testswithme.web.domain.trees.model.MutableTreeNode
import com.github.aivanovski.testswithme.web.domain.trees.model.NodeType
import com.github.aivanovski.testswithme.web.domain.trees.model.TreeNode
import com.github.aivanovski.testswithme.web.entity.Uid

object TreeDsl {

    fun tree(
        uid: Uid,
        name: String? = null,
        content: (TreeBuilder.() -> Unit)? = null
    ): TreeNode {
        val node = TreeBuilder(
            uid = uid,
            path = name ?: uid.toString()
        )
            .apply {
                content?.invoke(this)
            }
            .build()

        return node
    }

    class TreeBuilder(
        private val uid: Uid,
        private val path: String
    ) {

        private val nodes = mutableListOf<MutableTreeNode>()

        fun branch(
            uid: Uid,
            name: String? = null,
            content: (TreeBuilder.() -> Unit)? = null
        ) {
            val nodeName = name ?: uid.toString()
            val node = TreeBuilder(
                uid = uid,
                path = "$path/$nodeName"
            )
                .apply {
                    content?.invoke(this)
                }
                .build()

            nodes.add(node)
        }

        fun leaf(
            uid: Uid,
            name: String? = null
        ) {
            val nodeName = name ?: uid.toString()
            nodes.add(
                MutableTreeNode(
                    path = "$path/$nodeName",
                    type = NodeType.LEAF,
                    entityUid = uid,
                    nodes = mutableListOf()
                )
            )
        }

        fun build(): MutableTreeNode =
            MutableTreeNode(
                path = path,
                type = NodeType.BRANCH,
                entityUid = uid,
                nodes = nodes
            )
    }
}