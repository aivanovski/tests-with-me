package com.github.aivanovski.testswithme.web.domain.trees

import com.github.aivanovski.testswithme.web.domain.trees.model.DiffEvent
import com.github.aivanovski.testswithme.web.utils.TestNodes.A1
import com.github.aivanovski.testswithme.web.utils.TestNodes.A2
import com.github.aivanovski.testswithme.web.utils.TestNodes.B2
import com.github.aivanovski.testswithme.web.utils.TestNodes.L1
import com.github.aivanovski.testswithme.web.utils.TestNodes.L2
import com.github.aivanovski.testswithme.web.utils.TestNodes.L3
import com.github.aivanovski.testswithme.web.utils.TestNodes.L4
import com.github.aivanovski.testswithme.web.utils.TestNodes.NEW_LEAF
import com.github.aivanovski.testswithme.web.utils.TestNodes.OLD_LEAF
import com.github.aivanovski.testswithme.web.utils.TestNodes.ROOT
import com.github.aivanovski.testswithme.web.utils.TreeDsl.tree
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TreeDifferTest {

    @Test
    fun `getDiff should detect insertions`() {
        // act
        val diff = TreeDiffer.getDiff(
            lhs = LEFT_TREE,
            rhs = RIGHT_TREE,
            isContentChanged = { lhsNode, rhsNode -> false }
        )

        // assert
        diff.size shouldBe 3

        (diff[0] as DiffEvent.Insert).node.entityUid shouldBe B2
        (diff[1] as DiffEvent.Insert).node.entityUid shouldBe L4
        (diff[2] as DiffEvent.Insert).node.entityUid shouldBe L3
    }

    @Test
    fun `getDiff should detect node deletions`() {
        // act
        val diff = TreeDiffer.getDiff(
            lhs = RIGHT_TREE,
            rhs = LEFT_TREE,
            isContentChanged = { lhsNode, rhsNode -> false }
        )

        // assert
        diff.size shouldBe 3

        (diff[0] as DiffEvent.Delete).node.entityUid shouldBe B2
        (diff[1] as DiffEvent.Delete).node.entityUid shouldBe L4
        (diff[2] as DiffEvent.Delete).node.entityUid shouldBe L3
    }

    @Test
    fun `getDiff should detect updates`() {
        // act
        val diff = TreeDiffer.getDiff(
            lhs = OLD_TREE,
            rhs = NEW_TREE,
            isContentChanged = { lhs, rhs ->
                // Node L2 should be marked as modified
                lhs.entityUid == OLD_LEAF && rhs.entityUid == NEW_LEAF
            }
        )

        // assert
        diff.size shouldBe 1

        val event = (diff.first() as DiffEvent.Update)
        event.oldNode.entityUid shouldBe OLD_LEAF
        event.newNode.entityUid shouldBe NEW_LEAF
    }

    companion object {
        private val LEFT_TREE = tree(ROOT) {
            branch(A1) {
                leaf(L1)
                leaf(L2)
            }
            branch(A2)
        }

        private val RIGHT_TREE = tree(ROOT) {
            branch(A1) {
                leaf(L1)
                leaf(L2)
            }
            branch(A2) {
                branch(B2) {
                    leaf(L4)
                }
                leaf(L3)
            }
        }

        private val OLD_TREE = tree(ROOT) {
            branch(A1) {
                leaf(uid = OLD_LEAF, name = "Leaf")
            }
        }

        private val NEW_TREE = tree(ROOT) {
            branch(A1) {
                leaf(uid = NEW_LEAF, name = "Leaf")
            }
        }
    }
}