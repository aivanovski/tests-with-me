package com.github.aivanovski.testswithme.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry

@Dao
interface GroupEntryDao {

    @Query("SELECT * FROM group_entry")
    fun getAll(): List<GroupEntry>

    @Insert
    fun insert(group: GroupEntry)

    @Update
    fun update(group: GroupEntry)

    @Query("DELETE FROM group_entry")
    fun removeAll()

    @Query("DELETE FROM group_entry WHERE uid = :uid")
    fun removeByUid(uid: String)
}