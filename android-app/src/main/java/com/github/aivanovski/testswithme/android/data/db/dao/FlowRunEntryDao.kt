package com.github.aivanovski.testswithme.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry

@Dao
interface FlowRunEntryDao {

    @Query("SELECT * FROM flow_run_entry")
    fun getAll(): List<FlowRunEntry>

    @Insert
    fun insert(run: FlowRunEntry)

    @Update
    fun update(run: FlowRunEntry)

    @Query("DELETE FROM flow_run_entry WHERE uid = :uid")
    fun removeByUid(uid: String)
}