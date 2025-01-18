package com.github.aivanovski.testswithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class Sha256HashDto(
    val value: String
)