package com.github.aivanovski.testwithme.android.utils

import com.github.aivanovski.testwithme.android.entity.db.JobEntry
import com.github.aivanovski.testwithme.android.entity.db.JobHistoryEntry

fun JobEntry.toHistoryEntry(id: Long? = null): JobHistoryEntry {
    return JobHistoryEntry(
        id = id ?: this.id,
        uid = uid,
        flowUid = flowUid,
        currentStepUid = currentStepUid,
        addedTimestamp = addedTimestamp,
        finishedTimestamp = finishedTimestamp,
        executionTime = executionTime,
        executionResult = executionResult,
        status = status,
        onFinishAction = onFinishAction
    )
}

fun JobHistoryEntry.toEntry(): JobEntry {
    return JobEntry(
        id = id,
        uid = uid,
        flowUid = flowUid,
        currentStepUid = currentStepUid,
        addedTimestamp = addedTimestamp,
        finishedTimestamp = finishedTimestamp,
        executionTime = executionTime,
        executionResult = executionResult,
        status = status,
        onFinishAction = onFinishAction
    )
}