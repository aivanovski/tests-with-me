package com.github.aivanovski.testswithme.domain.fomatters

import com.github.aivanovski.testswithme.entity.UiEntity
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.ellipsize
import com.github.aivanovski.testswithme.extensions.removeEmptyNodes
import com.github.aivanovski.testswithme.extensions.removeEmptyParents
import com.github.aivanovski.testswithme.extensions.toShortString
import com.github.aivanovski.testswithme.extensions.visitWithDepth
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.utils.StringUtils.DOTS

class CompactNodeFormatter(
    private val isPrintBounds: Boolean = false,
    private val maxStringLength: Int = 30
) : UiNodeFormatter {

    override fun format(uiNode: UiNode<*>): String {
        val lines = mutableListOf<String>()

        val isEmpty = { entity: UiEntity ->
            entity.text.isNullOrEmpty() &&
                entity.contentDescription.isNullOrEmpty()
        }

        val cleanedTree = uiNode
            .removeEmptyNodes(isEmptyPredicate = isEmpty)
            .removeEmptyParents(isEmptyPredicate = isEmpty)

        cleanedTree.visitWithDepth { node, depth ->
            val indent = "  ".repeat(depth)
            lines.add("$indent${node.formatNode()}")
        }

        return lines.joinToString(separator = "\n")
    }

    private fun UiNode<*>.formatNode(): String {
        return buildString {
            val className = entity.className ?: StringUtils.EMPTY

            val lastDot = className.lastIndexOf(".")
            if (lastDot != -1) {
                append(className.substring(lastDot + 1))
            } else {
                append(className)
            }

            val hasContent =
                (!entity.text.isNullOrEmpty() || !entity.contentDescription.isNullOrEmpty())

            if (hasContent) {
                append(" [")

                appendWhen(!entity.text.isNullOrEmpty()) {
                    val text = entity.text?.ellipsize(
                        maxLength = maxStringLength,
                        ending = DOTS
                    )

                    "text=$text"
                }

                appendWhen(!entity.contentDescription.isNullOrEmpty()) {
                    val contentDescription = entity.contentDescription?.ellipsize(
                        maxLength = maxStringLength,
                        ending = DOTS
                    )
                    "cd=${contentDescription}"
                }

                appendWhen(entity.isClickable == true) { "clickable" }

                if (isPrintBounds) {
                    appendWhen(entity.bounds != null) {
                        "bounds=${entity.bounds?.toShortString()}"
                    }
                }

                append("]")
            }
        }
    }

    inline fun StringBuilder.appendWhen(
        predicate: Boolean,
        content: () -> String
    ) {
        if (predicate) {
            if (!endsWith("[")) {
                append(SEPARATOR)
            }
            append(content.invoke())
        }
    }

    companion object {
        const val SEPARATOR = ", "
    }
}