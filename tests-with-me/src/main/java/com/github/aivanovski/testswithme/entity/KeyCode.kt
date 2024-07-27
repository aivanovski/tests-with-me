package com.github.aivanovski.testswithme.entity

import kotlinx.serialization.Serializable

@Serializable
sealed interface KeyCode {

    @Serializable
    object Back : KeyCode

    @Serializable
    object Home : KeyCode
}