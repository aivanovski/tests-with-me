package com.github.aivanovski.testswithme.web.domain.trees

import com.github.aivanovski.testswithme.web.domain.trees.model.DiffEvent
import com.github.aivanovski.testswithme.web.domain.trees.model.NodeType
import com.github.aivanovski.testswithme.web.domain.trees.model.TreeNode

object TreeDiffer {

    fun getDiff(
        lhs: TreeNode,
        rhs: TreeNode,
        isContentChanged: (lhs: TreeNode, rhs: TreeNode) -> Boolean
    ): List<DiffEvent> = getDiff(
        lhsRoot = lhs,
        rhsRoot = rhs,
        visited = HashSet(),
        isContentChanged = isContentChanged
    )

    private fun getDiff(
        lhsRoot: TreeNode,
        rhsRoot: TreeNode,
        visited: MutableSet<String>,
        isContentChanged: (lhsNode: TreeNode, rhsNode: TreeNode) -> Boolean
    ): List<DiffEvent> {
        val allLhsNodes = lhsRoot.traverse()
        val allRhsNodes = rhsRoot.traverse()

        val lhsNodeMap = allLhsNodes.associateBy { node -> node.path }
        val rhsNodeMap = allRhsNodes.associateBy { node -> node.path }

        val allPath = HashSet<String>()
            .apply {
                addAll(lhsNodeMap.keys)
                addAll(rhsNodeMap.keys)
            }

        val events = mutableListOf<DiffEvent>()

        for (path in allPath) {
            val lhs = lhsNodeMap[path]
            val rhs = rhsNodeMap[path]

            val isLhsVisited = lhs?.path in visited
            val isRhsVisited = rhs?.path in visited

            when {
                // item was changed
                lhs != null && rhs != null && !isLhsVisited && !isRhsVisited -> {
                    if (lhs.type == NodeType.LEAF
                        && rhs.type == NodeType.LEAF
                        && isContentChanged.invoke(lhs, rhs)
                    ) {
                        events.add(
                            DiffEvent.Update(
                                oldNode = lhs,
                                newNode = rhs
                            )
                        )
                    }
                }

                // item was removed
                lhs != null && rhs == null && !isLhsVisited -> {
                    events.add(DiffEvent.Delete(lhs))
                }

                // item was added
                lhs == null && rhs != null && !isRhsVisited -> {
                    events.add(DiffEvent.Insert(rhs))
                }
            }

            lhs?.let { node -> visited.add(node.path) }
            rhs?.let { node -> visited.add(node.path) }
        }

        return events
    }
}