package com.github.aivanovski.testswithme.domain.fomatters

import com.github.aivanovski.testswithme.entity.UiEntity
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.format
import io.kotest.matchers.shouldBe

import org.junit.jupiter.api.Test

class CliUiNodeFormatterTest {

    @Test
    fun `format should work`() {
        format(buildTree()) shouldBe """
            View3
              View6
                TextView [text=Completion]
              View9
                TextView [text=1.11.0]
        """.trimIndent()
    }

    private fun format(tree: UiNode<Unit>): String =
        tree.format(CompactNodeFormatter())

    private fun buildTree(): UiNode<Unit> {
        val root = newNode(className = "FrameLayout")
        val v1 = newNode(className = "View1")
        val v2 = newNode(className = "View2")
        val v3 = newNode(className = "View3")

        val v4 = newNode(className = "View4")
        val v5 = newNode(className = "View5")

        val v6 = newNode(className = "View6")
        val t1 = newNode(className = "TextView", text = "Completion")
        val v7 = newNode(className = "View7", isClickable = true)

        val v8 = newNode(className = "View8")

        val v9 = newNode(className = "View9")
        val v10 = newNode(className = "View10")
        val t2 = newNode(className = "TextView", text = "1.11.0")

        root.nodes.add(v1)
        v1.nodes.add(v2)
        v2.nodes.add(v3)

        v3.nodes.add(v4)
        v4.nodes.add(v5)

        v3.nodes.add(v6)
        v6.nodes.add(t1)
        v6.nodes.add(v7)

        v3.nodes.add(v8)

        v3.nodes.add(v9)
        v9.nodes.add(v10)
        v10.nodes.add(t2)

        return root
    }

    private fun newNode(
        className: String? = null,
        text: String? = null,
        contentDescription: String? = null,
        isEnabled: Boolean? = null,
        isEditable: Boolean? = null,
        isFocused: Boolean? = null,
        isFocusable: Boolean? = null,
        isClickable: Boolean? = null,
        isLongClickable: Boolean? = null,
        isCheckable: Boolean? = null,
        isChecked: Boolean? = null,
    ): UiNode<Unit> =
        UiNode(
            source = Unit,
            entity = UiEntity(
                resourceId = null,
                packageName = "com.example.application",
                className = className,
                bounds = null,
                text = text,
                contentDescription = contentDescription,
                isEnabled = isEnabled,
                isEditable = isEditable,
                isFocused = isFocused,
                isFocusable = isFocusable,
                isClickable = isClickable,
                isLongClickable = isLongClickable,
                isCheckable = isCheckable,
                isChecked = isChecked
            ),
            nodes = mutableListOf()
        )
}