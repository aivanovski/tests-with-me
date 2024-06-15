package com.github.aivanovski.testwithme.extensions

import com.github.aivanovski.testwithme.entity.UiEntity
import com.github.aivanovski.testwithme.entity.UiElementSelector
import com.github.aivanovski.testwithme.entity.UiElementSelector.SelectionType
import com.github.aivanovski.testwithme.utils.StringUtils

// TODO: move to UiElementSelector extensions
fun UiEntity.matches(element: UiElementSelector): Boolean {
    val node = this

    return when (element.type) {
        SelectionType.ID -> {
            node.resourceId == "${node.packageName}:id/${element.id}"
        }

        SelectionType.TEXT -> {
            node.text == element.text
        }

        SelectionType.CONTAINS_TEXT -> {
            val targetText = element.containsText ?: StringUtils.EMPTY
            node.text != null && node.text.contains(
                targetText,
                ignoreCase = element.isIgnoreTextCase
            )
        }

        SelectionType.CONTENT_DESCRIPTION -> {
            node.contentDescription == element.contentDescription
        }

        SelectionType.FOCUSED -> {
            node.isFocused != null && node.isFocused == element.isFocused
        }

        SelectionType.CLICKABLE -> {
            node.isClickable != null && node.isClickable == element.isClickable
        }

        SelectionType.LONG_CLICKABLE -> {
            node.isLongClickable != null && node.isLongClickable == element.isLongClickable
        }
    }
}