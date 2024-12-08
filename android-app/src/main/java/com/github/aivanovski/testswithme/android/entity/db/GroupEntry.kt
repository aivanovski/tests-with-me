package com.github.aivanovski.testswithme.android.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aivanovski.testswithme.android.entity.TreeEntity

@Entity("group_entry")
data class GroupEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long? = null,

    @ColumnInfo("uid")
    override val uid: String,

    @ColumnInfo("parent_uid")
    val parentUid: String?,

    @ColumnInfo("project_uid")
    val projectUid: String,

    @ColumnInfo("name")
    val name: String
) : TreeEntity