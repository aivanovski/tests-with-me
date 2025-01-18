package com.github.aivanovski.testswithme.entity

import kotlinx.serialization.Serializable

@Serializable
data class UiEntity(
    val resourceId: String?,
    val packageName: String?,
    val className: String?,
    val bounds: Bounds?,
    val text: String?,
    val contentDescription: String?,
    val isEnabled: Boolean?,
    val isEditable: Boolean?,
    val isFocused: Boolean?,
    val isFocusable: Boolean?,
    val isClickable: Boolean?,
    val isLongClickable: Boolean?,
    val isCheckable: Boolean?,
    val isChecked: Boolean?
)