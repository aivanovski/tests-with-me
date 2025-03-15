package com.github.aivanovski.testswithme.android.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("project_entry")
data class ProjectEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long? = null,

    @ColumnInfo("uid")
    val uid: String,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("description")
    val description: String,

    @ColumnInfo("packageName")
    val packageName: String,

    @ColumnInfo("downloadUrl")
    val downloadUrl: String,

    @ColumnInfo("imageUrl")
    val imageUrl: String?,

    @ColumnInfo("siteUrl")
    val siteUrl: String?,

    @ColumnInfo("repositoryUrl")
    val repositoryUrl: String?
)