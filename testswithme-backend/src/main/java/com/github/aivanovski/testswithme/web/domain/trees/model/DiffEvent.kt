package com.github.aivanovski.testswithme.web.domain.trees.model

sealed class DiffEvent {

    data class Insert(
        val node: TreeNode
    ) : DiffEvent()

    data class Delete(
        val node: TreeNode
    ) : DiffEvent()

    data class Update(
        val oldNode: TreeNode,
        val newNode: TreeNode
    ) : DiffEvent()
}