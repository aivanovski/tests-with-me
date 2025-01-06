package com.github.aivanovski.testswithme.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aivanovski.testswithme.android.entity.db.JobHistoryEntry

@Dao
interface JobHistoryDao {

    @Query("SELECT * FROM job_history_entry")
    fun getAll(): List<JobHistoryEntry>

    @Query("SELECT * FROM job_history_entry WHERE uid = :uid")
    fun getByUid(uid: String): JobHistoryEntry?

    @Insert
    fun insert(job: JobHistoryEntry)

    @Update
    fun update(job: JobHistoryEntry)

    @Query("DELETE FROM job_history_entry WHERE uid = :uid")
    fun removeByUid(uid: String)

    @Query("DELETE FROM job_history_entry")
    fun removeAll()
}