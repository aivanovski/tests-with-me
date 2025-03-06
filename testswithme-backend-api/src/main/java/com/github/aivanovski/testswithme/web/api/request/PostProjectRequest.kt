package com.github.aivanovski.testswithme.web.api.request

import kotlinx.serialization.Serializable

@Serializable
data class PostProjectRequest(
    val packageName: String,
    val name: String,
    val description: String?,
    val downloadUrl: String,
    val imageUrl: String?,
    val siteUrl: String?,
    val repositoryUrl: String?
)