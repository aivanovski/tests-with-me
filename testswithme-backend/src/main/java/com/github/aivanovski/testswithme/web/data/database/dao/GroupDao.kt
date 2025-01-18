package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.Uid

class GroupDao(
    db: AppDatabase
) : Dao<Group>(
    db = db,
    entityType = Group::class.java,
    entityName = "Group"
) {

    fun getByProjectUid(projectUid: Uid): List<Group> {
        val variableName = entityName.uppercase().first()
        return db.execTransaction {
            createQuery(
                "From $entityName $variableName WHERE $variableName.projectUid = :uid",
                entityType
            )
                .setParameter("uid", projectUid)
                .resultList
        }
    }
}