package com.github.aivanovski.testswithme.android.gatewayServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class UiEntityDto(
    val packageName: String?,
    val className: String?,
    val bounds: UiBoundsDto?,
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