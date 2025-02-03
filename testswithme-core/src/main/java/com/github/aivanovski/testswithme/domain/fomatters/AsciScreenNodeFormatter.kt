package com.github.aivanovski.testswithme.domain.fomatters

import com.github.aivanovski.testswithme.domain.fomatters.AsciScreen.Node
import com.github.aivanovski.testswithme.entity.UiEntity
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.removeEmptyNodes
import com.github.aivanovski.testswithme.extensions.removeEmptyParents
import com.github.aivanovski.testswithme.extensions.traverseAndCollect

class AsciScreenNodeFormatter(
    screenPixelWidth: Int,
    screenPixelHeight: Int,
    screenCharWidth: Int,
    screenCharHeight: Int
) : UiNodeFormatter {

    private val screen = AsciScreen(
        screenPixelWidth,
        screenPixelHeight,
        screenCharWidth,
        screenCharHeight
    )

    override fun format(uiNode: UiNode<*>): String {
        val isEmpty = { entity: UiEntity ->
            entity.text.isNullOrEmpty() &&
                entity.contentDescription.isNullOrEmpty()
        }

        val cleanedTree = uiNode
            .removeEmptyNodes(isEmptyPredicate = isEmpty)
            .removeEmptyParents(isEmptyPredicate = isEmpty)

        val nodes = cleanedTree.convertNodes(screen)

        screen.clear()
        screen.render(nodes)

        return screen.getContent()
    }

    private fun UiNode<*>.convertNodes(
        screen: AsciScreen
    ): List<Node> {
        val root = this

        return root.traverseAndCollect { true }
            .filter { node ->
                (!node.entity.text.isNullOrEmpty() || !node.entity.contentDescription.isNullOrEmpty())
                    && node.entity.bounds != null
            }
            .mapNotNull { node ->
                val originalBounds = node.entity.bounds
                    ?: return@mapNotNull null

                val text = when {
                    !node.entity.text.isNullOrEmpty() -> node.entity.text

                    !node.entity.contentDescription.isNullOrEmpty() ->
                        node.entity.contentDescription

                    else -> return@mapNotNull null
                }

                val bounds = screen.convertBounds(originalBounds)
                    ?: return@mapNotNull null

                Node(
                    text = text,
                    bounds = bounds
                )
            }
    }
}