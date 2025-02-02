package com.github.aivanovski.testswithme.android.domain.flow

import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.utils.mutableStateFlow
import kotlin.time.Duration.Companion.seconds

class UiTreeCollector {

    private var uiTree by mutableStateFlow<UiTreeHolder?>(null)

    fun setUiTree(tree: UiNode<Unit>) {
        uiTree = UiTreeHolder(
            tree = tree,
            timestamp = System.currentTimeMillis()
        )
    }

    fun getUiTree(): UiNode<Unit>? {
        val uiTree = uiTree ?: return null

        val currentTimestamp = System.currentTimeMillis()
        return if (uiTree.timestamp < currentTimestamp + TIMEOUT) {
            uiTree.tree
        } else {
            null
        }
    }

    private data class UiTreeHolder(
        val tree: UiNode<Unit>,
        val timestamp: Long
    )

    companion object {
        private val TIMEOUT = 60.seconds.inWholeMilliseconds
    }
}