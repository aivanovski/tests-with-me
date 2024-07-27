package com.github.aivanovski.testswithme.android.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.entity.Flow

@Entity("flow_entry")
data class FlowEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long? = null,

    @ColumnInfo("uid")
    val uid: String,

    @ColumnInfo("project_uid")
    val projectUid: String,

    @ColumnInfo("group_uid")
    val groupUid: String?,

    @ColumnInfo("name")
    override val name: String,

    @ColumnInfo("source_type")
    val sourceType: SourceType
) : Flow