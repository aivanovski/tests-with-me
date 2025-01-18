package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid

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