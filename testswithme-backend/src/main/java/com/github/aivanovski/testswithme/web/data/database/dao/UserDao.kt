package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.User

class UserDao(
    db: AppDatabase
) : Dao<User>(
    db = db,
    entityType = User::class.java,
    entityName = "User_" // TODO: check if _ is necessary
) {

    fun findByName(name: String): User? {
        return findByField(
            fieldName = User.DbFields.NAME,
            fieldValue = name
        ).firstOrNull()
    }
}