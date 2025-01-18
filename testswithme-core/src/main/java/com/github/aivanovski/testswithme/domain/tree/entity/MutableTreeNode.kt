package com.github.aivanovski.testswithme.domain.tree.entity

data class MutableTreeNode(
    override val uid: String?,
    override val entity: Any?,
    override val nodes: MutableList<MutableTreeNode>
) : TreeNode