package com.github.aivanovski.testswithme.extensions

import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiElementSelector.SelectionType

fun UiElementSelector.toReadableFormat(): String {
    return when (type) {
        SelectionType.ID -> "[id = $id]"
        SelectionType.TEXT -> "[text = $text]"
        SelectionType.CONTAINS_TEXT -> "[has text = $text, ignoreCase = $isIgnoreTextCase]"
        SelectionType.CONTENT_DESCRIPTION -> "[content description = $contentDescription]"
        SelectionType.FOCUSED -> "[is in focus = $isFocused]"
        SelectionType.FOCUSABLE -> "[is focusable = $isFocusable]"
        SelectionType.CLICKABLE -> "[is clickable = $isClickable]"
        SelectionType.LONG_CLICKABLE -> "[is long clickable = $isLongClickable]"
        SelectionType.EDITABLE -> "[is editable = $isEditable]"
    }
}

fun List<UiElementSelector>.toReadableFormat(): String {
    return if (this.size == 1) {
        this.first().toReadableFormat()
    } else {
        this.joinToString(
            separator = ", ",
            prefix = "[",
            postfix = "]",
            transform = { element -> element.toReadableFormat() }
        )
    }
}