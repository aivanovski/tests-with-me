package com.github.aivanovski.testswithme.android.entity

data class MutableTreeNode(
    override val entity: Entity,
    override val nodes: MutableList<MutableTreeNode>
) : TreeNode