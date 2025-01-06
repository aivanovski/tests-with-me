package com.github.aivanovski.testswithme.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aivanovski.testswithme.android.entity.db.UserEntry

@Dao
interface UserEntryDao {

    @Query("SELECT * FROM user_entry")
    fun getAll(): List<UserEntry>

    @Insert
    fun insert(user: UserEntry)

    @Update
    fun update(user: UserEntry)

    @Query("DELETE FROM user_entry WHERE uid = :uid")
    fun removeByUid(uid: String)
}