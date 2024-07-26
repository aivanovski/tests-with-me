package com.github.aivanovski.testwithme.android.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aivanovski.testwithme.android.entity.SyncStatus

@Entity("local_step_run")
data class LocalStepRun(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long? = null,

    @ColumnInfo("job_uid")
    val jobUid: String,

    @ColumnInfo("flow_uid")
    val flowUid: String,

    @ColumnInfo("step_uid")
    val stepUid: String,

    @ColumnInfo("attempt_count")
    val attemptCount: Int,

    @ColumnInfo("is_last")
    val isLast: Boolean,

    @ColumnInfo("sync_status")
    val syncStatus: SyncStatus,

    @ColumnInfo("result")
    val result: String?
)