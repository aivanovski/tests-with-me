package com.github.aivanovski.testswithme.android.utils

import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.JobHistoryEntry

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
        flowRunUid = flowRunUid,
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
        flowRunUid = flowRunUid,
        onFinishAction = onFinishAction
    )
}