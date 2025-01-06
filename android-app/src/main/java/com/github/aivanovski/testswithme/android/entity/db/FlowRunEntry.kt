package com.github.aivanovski.testswithme.android.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("flow_run_entry")
data class FlowRunEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long? = null,

    @ColumnInfo("uid")
    val uid: String,

    @ColumnInfo("flow_uid")
    val flowUid: String,

    @ColumnInfo("user_uid")
    val userUid: String,

    @ColumnInfo("finished_at")
    val finishedAt: Long,

    @ColumnInfo("is_success")
    val isSuccess: Boolean,

    @ColumnInfo("app_version_name")
    val appVersionName: String,

    @ColumnInfo("app_version_code")
    val appVersionCode: String,

    @ColumnInfo("is_expired")
    val isExpired: Boolean
)