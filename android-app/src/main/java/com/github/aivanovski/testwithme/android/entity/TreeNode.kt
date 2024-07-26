package com.github.aivanovski.testwithme.android.entity

interface TreeNode {
    val entity: Entity
    val nodes: List<TreeNode>
}