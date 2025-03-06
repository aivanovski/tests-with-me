package com.github.aivanovski.testswithme.web.extensions

import com.github.aivanovski.testswithme.web.domain.usecases.GetTestSourcesToSyncUseCase
import com.github.aivanovski.testswithme.web.entity.TestSource

fun TestSource.isNecessaryToSync(): Boolean {
    val timeSinceLastSync = System.currentTimeMillis() - (lastCheckTimestamp?.milliseconds ?: 0)
    return lastCheckTimestamp == null ||
        timeSinceLastSync >= GetTestSourcesToSyncUseCase.SYNC_INTERVAL ||
        isForceSyncFlag
}