package com.github.aivanovski.testswithme.domain.tree.entity

interface TreeNode {
    val uid: String?
    val entity: Any?
    val nodes: List<TreeNode>
}