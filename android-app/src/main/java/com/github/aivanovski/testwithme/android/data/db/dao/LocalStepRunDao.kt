package com.github.aivanovski.testwithme.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aivanovski.testwithme.android.entity.db.LocalStepRun

@Dao
interface LocalStepRunDao {

    @Query("SELECT * FROM local_step_run")
    fun getAll(): List<LocalStepRun>

    @Query("SELECT * FROM local_step_run WHERE " +
        "job_uid = :jobUid AND flow_uid = :flowUid AND step_uid = :stepUid")
    fun get(jobUid: String, flowUid: String, stepUid: String?): LocalStepRun?

    @Query("SELECT * FROM local_step_run WHERE job_uid = :jobUid")
    fun getByJobUid(jobUid: String): List<LocalStepRun>

    @Insert
    fun insert(entry: LocalStepRun)

    @Update
    fun update(entry: LocalStepRun)
}