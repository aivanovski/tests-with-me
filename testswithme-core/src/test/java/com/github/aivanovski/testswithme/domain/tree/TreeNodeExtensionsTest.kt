package com.github.aivanovski.testswithme.domain.tree

import com.github.aivanovski.testswithme.domain.tree.entity.TreeNode
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TreeNodeExtensionsTest {

    @Test
    fun `buildTree should work`() {
        val root = buildTree(NODES)

        val layers = root.treeLayers()
            .map { layer ->
                layer.unwrapValues()
                    .sorted()
            }

        layers shouldBe listOf(
            listOf(5),
            listOf(3, 8),
            listOf(1, 2, 4, 7),
            listOf(0)
        )
    }

    @Test
    fun `findNodeByUid should find node`() {
        val root = buildTree(NODES)

        val node = root.findNodeByUid(uid = "3")

        node?.entity shouldBe IntNode(3, 5)
    }

    @Test
    fun `getDescendantNodes should return all node children`() {
        val root = buildTree(NODES)

        val nodes = root.findNodeByUid(uid = "3")
            ?.getDescendantNodes()
            ?: emptyList()

        val values = nodes.unwrapValues()
            .sorted()

        values shouldBe listOf(0, 1, 2, 4)
    }

    private fun buildTree(nodes: List<IntNode>): TreeNode {
        return TreeBuilder.buildTree(
            entities = nodes,
            uidSelector = { node -> node.value.toString() },
            parentSelector = { node -> node.parent?.toString() }
        )
    }

    private fun List<TreeNode>.unwrapValues(): List<Int> {
        return this.map { node -> (node.entity as IntNode).value }
    }

    private data class IntNode(
        val value: Int,
        val parent: Int?
    )

    companion object {

        private val NODES = listOf(
            node(5 to null),
            node(3 to 5),
            node(1 to 3),
            node(2 to 3),
            node(4 to 3),
            node(0 to 1),
            node(8 to 5),
            node(7 to 8)
        )

        private fun node(pair: Pair<Int, Int?>): IntNode = IntNode(pair.first, pair.second)
    }
}