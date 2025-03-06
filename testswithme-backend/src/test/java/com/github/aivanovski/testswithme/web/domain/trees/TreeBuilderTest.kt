package com.github.aivanovski.testswithme.web.domain.trees

import com.github.aivanovski.testswithme.extensions.unwrapOrReport
import com.github.aivanovski.testswithme.web.domain.trees.model.TreeEntity.TreeBranch
import com.github.aivanovski.testswithme.web.domain.trees.model.TreeEntity.TreeLeaf
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.utils.TestNodes.A1
import com.github.aivanovski.testswithme.web.utils.TestNodes.A2
import com.github.aivanovski.testswithme.web.utils.TestNodes.B1
import com.github.aivanovski.testswithme.web.utils.TestNodes.L1
import com.github.aivanovski.testswithme.web.utils.TestNodes.L2
import com.github.aivanovski.testswithme.web.utils.TestNodes.L3
import com.github.aivanovski.testswithme.web.utils.TestNodes.L4
import com.github.aivanovski.testswithme.web.utils.TestNodes.L5
import com.github.aivanovski.testswithme.web.utils.TestNodes.ROOT
import com.github.aivanovski.testswithme.web.utils.TreeDsl
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TreeBuilderTest {

    @Test
    fun `buildTree should build valid tree`() {
        // arrange
        val entities = listOf(
            newBranch(uid = ROOT, parentUid = null),
            newBranch(uid = A1, parentUid = ROOT),
            newBranch(uid = A2, parentUid = ROOT),
            newBranch(uid = B1, parentUid = A1),

            newLeaf(uid = L1, parentUid = A1),
            newLeaf(uid = L2, parentUid = A1),
            newLeaf(uid = L3, parentUid = A2),
            newLeaf(uid = L4, parentUid = A2),
            newLeaf(uid = L5, parentUid = B1)
        )

        val dslTree = TreeDsl.tree(ROOT) {
            branch(A1) {
                branch(B1) {
                    leaf(L5)
                }
                leaf(L1)
                leaf(L2)
            }
            branch(A2) {
                leaf(L3)
                leaf(L4)
            }
        }

        // act
        val tree = TreeBuilder.buildTree(
            entities = entities
        ).unwrapOrReport()

        // assert
        (tree == dslTree) shouldBe true
    }

    private fun newBranch(
        uid: Uid,
        parentUid: Uid?
    ) = TreeBranch(
        uid = uid,
        parentUid = parentUid,
        name = uid.toString()
    )

    private fun newLeaf(
        uid: Uid,
        parentUid: Uid?
    ) = TreeLeaf(
        uid = uid,
        parentUid = parentUid,
        name = uid.toString()
    )
}