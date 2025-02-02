package com.github.aivanovski.testswithme.domain.fomatters

import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.toShortString
import com.github.aivanovski.testswithme.extensions.visitWithDepth
import java.lang.StringBuilder

class RegularNodeFormatter : UiNodeFormatter {

    override fun format(uiNode: UiNode<*>): String {
        val lines = mutableListOf<String>()

        uiNode.visitWithDepth { node, depth ->
            val indent = "  ".repeat(depth)
            lines.add("$indent${node.formatNode()}")
        }

        return lines.joinToString(separator = "\n")
    }

    private fun UiNode<*>.formatNode(): String {
        val node = this

        return StringBuilder()
            .apply {
                val className = node.entity.className

                val text = node.entity.text
                val cd = node.entity.contentDescription
                val bounds = node.entity.bounds?.toShortString()
                val isEditable = node.entity.isEditable
                val isFocused = node.entity.isFocused
                val isClickable = node.entity.isClickable
                val childCount = node.nodes.size

                if (className != null) {
                    val lastDot = className.lastIndexOf(".")
                    if (lastDot != -1) {
                        append(className.substring(lastDot + 1))
                    } else {
                        append(className)
                    }
                }

                append("[")

                if (childCount > 0) {
                    append("children=$childCount")
                }

                if (node.entity.resourceId != null) {
                    appendWithSeparator(
                        "id=${node.entity.resourceId}",
                        DUMP_SEPARATOR,
                        DUMP_SEPARATOR_INDICATOR
                    )
                }

                if (text != null) {
                    appendWithSeparator(
                        "text=$text",
                        DUMP_SEPARATOR,
                        DUMP_SEPARATOR_INDICATOR
                    )
                }

                if (cd != null) {
                    appendWithSeparator(
                        "contDesc=$cd",
                        DUMP_SEPARATOR,
                        DUMP_SEPARATOR_INDICATOR
                    )
                }

                if (bounds != null) {
                    appendWithSeparator(
                        "bounds=$bounds",
                        DUMP_SEPARATOR,
                        DUMP_SEPARATOR_INDICATOR
                    )
                }

                if (isEditable == true) {
                    appendWithSeparator(
                        "EDITABLE",
                        DUMP_SEPARATOR,
                        DUMP_SEPARATOR_INDICATOR
                    )
                }

                if (isFocused == true) {
                    appendWithSeparator(
                        "FOCUSED",
                        DUMP_SEPARATOR,
                        DUMP_SEPARATOR_INDICATOR
                    )
                }

                if (isClickable == true) {
                    appendWithSeparator(
                        "CLICKABLE",
                        DUMP_SEPARATOR,
                        DUMP_SEPARATOR_INDICATOR
                    )
                }

                append("]")
            }
            .toString()
    }

    private fun StringBuilder.appendWithSeparator(
        value: String,
        separator: String,
        separatorIndicator: String
    ) {
        if (!this.endsWith(separatorIndicator)) {
            append(separator)
        }
        append(value)
    }

    companion object {
        private const val DUMP_SEPARATOR = ", "
        private const val DUMP_SEPARATOR_INDICATOR = "["
    }
}