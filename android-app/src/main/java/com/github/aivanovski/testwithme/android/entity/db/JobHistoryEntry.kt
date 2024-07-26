package com.github.aivanovski.testwithme.android.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aivanovski.testwithme.android.entity.ExecutionResult
import com.github.aivanovski.testwithme.android.entity.JobStatus
import com.github.aivanovski.testwithme.android.entity.OnFinishAction

@Entity("job_history_entry")
data class JobHistoryEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long? = null,

    @ColumnInfo("uid")
    val uid: String,

    @ColumnInfo("flow_uid")
    val flowUid: String,

    @ColumnInfo("current_step_uid")
    val currentStepUid: String,

    @ColumnInfo("added_timestamp")
    val addedTimestamp: Long,

    @ColumnInfo("finished_timestamp")
    val finishedTimestamp: Long?,

    @ColumnInfo("execution_time")
    val executionTime: Long?,

    @ColumnInfo("execution_result")
    val executionResult: ExecutionResult,

    @ColumnInfo("status")
    val status: JobStatus,

    @ColumnInfo("on_finish_action")
    val onFinishAction: OnFinishAction
)