package com.github.aivanovski.testwithme.entity

import kotlinx.serialization.Serializable

@Serializable
sealed interface KeyCode {

    @Serializable
    object Back : KeyCode

    @Serializable
    object Home : KeyCode
}