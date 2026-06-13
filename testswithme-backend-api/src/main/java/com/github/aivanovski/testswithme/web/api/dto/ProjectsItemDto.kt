package com.github.aivanovski.testswithme.web.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProjectsItemDto(
    val id: String,
    val rootGroupId: String,
    val packageName: String,
    val name: String,
    val description: String?,
    val downloadUrl: String,
    val imageUrl: String?,
    val siteUrl: String?,
    val repositoryUrl: String?,
    val lastSyncResult: SyncResultDto?
)