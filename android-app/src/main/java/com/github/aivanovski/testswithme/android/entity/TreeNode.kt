package com.github.aivanovski.testswithme.android.entity

interface TreeNode {
    val entity: Entity
    val nodes: List<TreeNode>
}