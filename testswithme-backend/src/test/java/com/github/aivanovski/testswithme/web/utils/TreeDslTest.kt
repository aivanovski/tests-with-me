package com.github.aivanovski.testswithme.web.utils

import com.github.aivanovski.testswithme.web.domain.trees.mapLayers
import com.github.aivanovski.testswithme.web.domain.trees.toUidTree
import com.github.aivanovski.testswithme.web.utils.TestNodes.ROOT
import com.github.aivanovski.testswithme.web.utils.TestNodes.A1
import com.github.aivanovski.testswithme.web.utils.TestNodes.A2
import com.github.aivanovski.testswithme.web.utils.TestNodes.B1
import com.github.aivanovski.testswithme.web.utils.TestNodes.B2
import com.github.aivanovski.testswithme.web.utils.TestNodes.C1
import com.github.aivanovski.testswithme.web.utils.TestNodes.L1
import com.github.aivanovski.testswithme.web.utils.TestNodes.L2
import com.github.aivanovski.testswithme.web.utils.TestNodes.L3
import com.github.aivanovski.testswithme.web.utils.TestNodes.L4
import com.github.aivanovski.testswithme.web.utils.TestNodes.L5
import com.github.aivanovski.testswithme.web.utils.TreeDsl.tree
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TreeDslTest {

    @Test
    fun `dsl should place nodes at valid place`() {
        val tree = tree(ROOT) {
            branch(A1) {
                branch(B1) {
                    leaf(L1)
                    leaf(L2)
                }
                leaf(L3)
                leaf(L4)
            }
            branch(A2) {
                branch(B2)
                leaf(L5)
            }
        }

        tree.toUidTree() shouldBe listOf(
            listOf(ROOT),
            listOf(A1, A2),
            listOf(B1, L3, L4, B2, L5),
            listOf(L1, L2)
        )
    }

    @Test
    fun `dsl should create a valid path`() {
        val tree = tree(ROOT) {
            branch(A1) {
                branch(B1) {
                    leaf(L1)
                    leaf(L2)
                }
                leaf(L3)
                leaf(L4)
            }
            branch(A2) {
                leaf(L5)
            }
        }

        val paths = tree.mapLayers { node -> node.path }
        paths shouldBe listOf(
            listOf("Root"),
            listOf("Root/A1", "Root/A2"),
            listOf("Root/A1/B1", "Root/A1/L3", "Root/A1/L4", "Root/A2/L5"),
            listOf("Root/A1/B1/L1", "Root/A1/B1/L2")
        )
    }

    @Test
    fun `test equal trees`() {
        val lhs = tree(ROOT) {
            branch(A1) {
                leaf(L1)
                leaf(L2)
            }
            branch(A2)
        }

        val rhs = tree(ROOT) {
            branch(A1) {
                leaf(L1)
                leaf(L2)
            }
            branch(A2)
        }

        (lhs == rhs) shouldBe true
    }
    @Test
    fun `test not equal trees`() {
        val lhs = tree(ROOT) {
            branch(A2) {
                leaf(L1)
                leaf(L2)
            }
            branch(A1)
        }

        val rhs = tree(ROOT) {
            branch(A1) {
                leaf(L1)
                leaf(L2)
            }
            branch(A2)
        }

        (lhs == rhs) shouldBe false
    }
}