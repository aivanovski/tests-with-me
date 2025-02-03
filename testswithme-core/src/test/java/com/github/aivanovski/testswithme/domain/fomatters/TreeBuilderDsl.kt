package com.github.aivanovski.testswithme.domain.fomatters

import com.github.aivanovski.testswithme.entity.UiNode

object TreeDsl {

    fun <T> tree(
        root: UiNode<T>,
        content: (TreeBuilder<T>.() -> Unit)? = null
    ): UiNode<T> {
        val node = TreeBuilder(root)
            .apply {
                content?.invoke(this)
            }
            .build()

        return node
    }

    class TreeBuilder<T>(
        private val root: UiNode<T>
    ) {

        fun node(
            node: UiNode<T>,
            content: (TreeBuilder<T>.() -> Unit)? = null
        ) {
            add(node, content)
        }

        fun build(): UiNode<T> {
            return root
        }

        private fun add(
            node: UiNode<T>,
            content: (TreeBuilder<T>.() -> Unit)? = null
        ) {
            val newNode = TreeBuilder(node)
                .apply {
                    content?.invoke(this)
                }
                .build()

            root.nodes.add(newNode)
        }
    }
}