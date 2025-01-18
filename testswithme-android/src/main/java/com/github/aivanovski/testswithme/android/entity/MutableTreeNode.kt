package com.github.aivanovski.testswithme.android.entity

data class MutableTreeNode(
    override val entity: TreeEntity,
    override val nodes: MutableList<MutableTreeNode>
) : TreeNode