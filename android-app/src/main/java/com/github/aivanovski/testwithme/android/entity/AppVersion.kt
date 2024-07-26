package com.github.aivanovski.testwithme.android.entity

import kotlinx.serialization.Serializable

@Serializable
data class AppVersion(
    val code: Int,
    val name: String
)