package com.github.aivanovski.testwithme.android.entity

data class MutableTreeNode(
    override val entity: Entity,
    override val nodes: MutableList<MutableTreeNode>
) : TreeNode