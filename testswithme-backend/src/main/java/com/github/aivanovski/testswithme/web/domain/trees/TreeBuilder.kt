package com.github.aivanovski.testswithme.web.domain.trees

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.web.domain.trees.model.MutableTreeNode
import com.github.aivanovski.testswithme.web.domain.trees.model.TreeEntity
import com.github.aivanovski.testswithme.web.domain.trees.model.TreeNode
import com.github.aivanovski.testswithme.web.domain.trees.model.getType
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import java.util.LinkedList

object TreeBuilder {

    fun buildTree(
        entities: List<TreeEntity>
    ): Either<AppException, TreeNode> =
        either {
            val uidToChildrenMap = buildUidToChildrenMap(
                entities = entities
            )

            val uidToPathMap = buildUidToPathMap(
                uidToChildrenMap = uidToChildrenMap,
            )

            val uidToNodeMap = buildUidToNodeMap(
                uidToChildrenMap = uidToChildrenMap,
                uidToPathMap = uidToPathMap
            )

            val roots = uidToChildrenMap[null] ?: emptyList()
            if (roots.size > 1) {
                raise(AppException("Tree should have one root node"))
            }

            val rootUid = roots.firstOrNull()?.uid
            val rootNode = uidToNodeMap[rootUid]
                ?: raise(AppException("Failed to find root node"))

            rootNode
        }

    private fun buildUidToNodeMap(
        uidToChildrenMap: Map<Uid?, List<TreeEntity>>,
        uidToPathMap: Map<Uid, String>,
        // typeSelector: (T) -> NodeType,
        // uidSelector: (T) -> Uid,
    ): Map<Uid, TreeNode> {
        val uidToNodeMap = HashMap<Uid, MutableTreeNode>()

        val rootEntities = uidToChildrenMap[null] ?: emptyList()

        fun fillMapDfs(
            entity: TreeEntity,
            uidToChildrenMap: Map<Uid?, List<TreeEntity>>,
            uidToPathMap: Map<Uid, String>,
            resultMap: MutableMap<Uid, MutableTreeNode>
        ) {
            val uid = entity.uid
            val children = uidToChildrenMap[uid] ?: emptyList()

            val nodes = if (children.isNotEmpty()) {
                val nodes = mutableListOf<MutableTreeNode>()

                for (child in children) {
                    fillMapDfs(
                        entity = child,
                        uidToChildrenMap = uidToChildrenMap,
                        uidToPathMap = uidToPathMap,
                        resultMap = resultMap
                    )

                    val childUid = child.uid
                    val childNode = resultMap[childUid] ?: continue

                    nodes.add(childNode)
                }

                nodes
            } else {
                mutableListOf()
            }

            val path = uidToPathMap[uid] ?: StringUtils.EMPTY

            resultMap[uid] = MutableTreeNode(
                path = path,
                type = entity.getType(),
                entityUid = uid,
                nodes = nodes
            )
        }

        for (entity in rootEntities) {
            fillMapDfs(
                entity = entity,
                uidToChildrenMap = uidToChildrenMap,
                uidToPathMap = uidToPathMap,
                resultMap = uidToNodeMap
            )
        }

        return uidToNodeMap
    }

    private fun buildUidToChildrenMap(
        entities: List<TreeEntity>
    ): Map<Uid?, List<TreeEntity>> {
        val uidToChildrenMap = HashMap<Uid?, MutableList<TreeEntity>>()

        for (entity in entities) {
            val parentUid = entity.parentUid
            uidToChildrenMap[parentUid] = uidToChildrenMap.getOrDefault(parentUid, mutableListOf())
                .apply {
                    add(entity)
                }
        }

        return uidToChildrenMap
    }

    private fun buildUidToPathMap(
        uidToChildrenMap: Map<Uid?, List<TreeEntity>>,
    ): Map<Uid, String> {
        val rootEntities = uidToChildrenMap[null] ?: emptyList()

        val queue = LinkedList<Pair<TreeEntity?, TreeEntity>>()
            .apply {
                for (entity in rootEntities) {
                    add(null to entity)
                }
            }

        val uidToPathMap = HashMap<Uid, String>()
        while (queue.isNotEmpty()) {
            repeat(queue.size) {
                val (parent, entity) = queue.poll()

                val parentUid = parent?.uid
                val parentPath = uidToPathMap[parentUid] ?: StringUtils.EMPTY
                val uid = entity.uid
                val name = entity.name

                val path = if (parentPath.isNotEmpty()) {
                    "$parentPath/$name"
                } else {
                    name
                }

                uidToPathMap[uid] = path

                val children = uidToChildrenMap[uid] ?: emptyList()
                for (child in children) {
                    queue.add(entity to child)
                }
            }
        }

        return uidToPathMap
    }
}