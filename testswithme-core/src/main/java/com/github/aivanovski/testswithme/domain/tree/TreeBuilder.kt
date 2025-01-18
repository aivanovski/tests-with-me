package com.github.aivanovski.testswithme.domain.tree

import com.github.aivanovski.testswithme.domain.tree.entity.MutableTreeNode
import com.github.aivanovski.testswithme.domain.tree.entity.TreeEntity
import com.github.aivanovski.testswithme.domain.tree.entity.TreeNode

object TreeBuilder {

    fun <T : Any> buildTree(
        entities: List<T>,
        uidSelector: (T) -> String,
        parentSelector: (T) -> String?
    ): TreeNode {
        val items = entities.map { entity ->
            EntityWrapper(
                entity = entity,
                uid = uidSelector.invoke(entity),
                parentUid = parentSelector.invoke(entity)
            )
        }

        val uidToNodeMap = HashMap<String, MutableTreeNode>()
        for (item in items) {
            uidToNodeMap[item.uid] = MutableTreeNode(
                uid = item.uid,
                entity = item,
                nodes = mutableListOf()
            )
        }

        val rootNodes = mutableListOf<MutableTreeNode>()
        for (item in items) {
            val itemNode = uidToNodeMap[item.uid] ?: continue
            val children = uidToNodeMap[item.parentUid]
                ?.nodes
                ?: rootNodes

            children.add(itemNode)
        }

        return if (rootNodes.size == 1) {
            rootNodes
                .first()
                .map<EntityWrapper<T>, T> { wrapper -> wrapper.entity }
        } else {
            MutableTreeNode(
                uid = null,
                entity = null,
                nodes = rootNodes
            )
                .map<EntityWrapper<T>, T> { wrapper -> wrapper.entity }
        }
    }

    private data class EntityWrapper<T>(
        val entity: T,
        override val uid: String,
        val parentUid: String?
    ) : TreeEntity
}