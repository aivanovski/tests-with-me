package com.github.aivanovski.testswithme.android.entity

interface TreeNode {
    val entity: TreeEntity
    val nodes: List<TreeNode>
}