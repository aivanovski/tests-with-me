package com.github.aivanovski.testswithme.web.domain.trees.model

import com.github.aivanovski.testswithme.web.entity.Uid

sealed interface TreeNode {
    val path: String
    val type: NodeType
    val entityUid: Uid
    val nodes: List<TreeNode>
}

data class MutableTreeNode(
    override val path: String,
    override val type: NodeType,
    override val entityUid: Uid,
    override val nodes: MutableList<MutableTreeNode>
) : TreeNode