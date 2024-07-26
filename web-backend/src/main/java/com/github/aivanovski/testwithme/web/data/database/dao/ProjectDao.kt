package com.github.aivanovski.testwithme.web.data.database.dao

import com.github.aivanovski.testwithme.web.data.database.AppDatabase
import com.github.aivanovski.testwithme.web.entity.Uid
import com.github.aivanovski.testwithme.web.entity.Project

class ProjectDao(
    db: AppDatabase
) : Dao<Project>(
    db = db,
    entityType = Project::class.java,
    entityName = "Project"
) {

    fun getByUserUid(userUid: Uid): List<Project> {
        val variableName = entityName.uppercase().first()
        return db.execTransaction {
            createQuery(
                "From $entityName $variableName WHERE $variableName.userUid = :uid",
                entityType
            )
                .setParameter("uid", userUid)
                .resultList
        }
    }
}