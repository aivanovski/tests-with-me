package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.User

class UserDao(
    db: AppDatabase
) : Dao<User>(
    db = db,
    entityType = User::class.java,
    entityName = "User_"
) {

    fun findByName(name: String): User? {
        return findByField(
            fieldName = "name",
            fieldValue = name
        ).firstOrNull()
    }
}