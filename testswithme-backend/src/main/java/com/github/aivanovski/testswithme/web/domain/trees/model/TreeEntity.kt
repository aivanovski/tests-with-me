package com.github.aivanovski.testswithme.web.domain.trees.model

import com.github.aivanovski.testswithme.web.entity.Uid

sealed interface TreeEntity {
    val uid: Uid
    val parentUid: Uid?
    val name: String

    data class TreeBranch(
        override val uid: Uid,
        override val parentUid: Uid?,
        override val name: String
    ) : TreeEntity

    data class TreeLeaf(
        override val uid: Uid,
        override val parentUid: Uid?,
        override val name: String,
    ) : TreeEntity
}

fun TreeEntity.getType(): NodeType = when (this) {
    is TreeEntity.TreeBranch -> NodeType.BRANCH
    is TreeEntity.TreeLeaf -> NodeType.LEAF
}