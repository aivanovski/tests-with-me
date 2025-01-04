package com.github.aivanovski.testswithme.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry

@Dao
interface ProjectEntryDao {

    @Query("SELECT * FROM project_entry")
    fun getAll(): List<ProjectEntry>

    @Query("SELECT * FROM project_entry WHERE uid = :uid")
    fun getByUid(uid: String): ProjectEntry?

    @Insert
    fun insert(flow: ProjectEntry)

    @Update
    fun update(flow: ProjectEntry)

    @Query("DELETE FROM project_entry WHERE uid = :uid")
    fun removeByUid(uid: String)

    @Query("DELETE FROM project_entry")
    fun removeAll()
}