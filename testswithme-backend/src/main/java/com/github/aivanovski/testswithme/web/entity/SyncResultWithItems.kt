package com.github.aivanovski.testswithme.web.entity

data class SyncResultWithItems(
    val result: SyncResult,
    val items: List<ProcessedSyncItem>
)