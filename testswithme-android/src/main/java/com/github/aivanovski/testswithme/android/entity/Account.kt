package com.github.aivanovski.testswithme.android.entity

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val uid: String,
    val name: String,
    val password: String
)