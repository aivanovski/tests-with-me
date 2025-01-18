package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.Uid

abstract class Dao<T>(
    protected val db: AppDatabase,
    protected val entityType: Class<T>,
    protected val entityName: String
) {

    fun getAll(): List<T> {
        return db.execTransaction {
            createQuery("From $entityName", entityType)
                .resultList
        }
    }

    fun findByUid(uid: Uid): T? {
        val variableName = entityName.uppercase().first()
        return db.execTransaction {
            createQuery("From $entityName $variableName WHERE $variableName.uid = :uid", entityType)
                .setParameter("uid", uid)
                .resultList
                .firstOrNull()
        }
    }

    fun findByField(
        fieldName: String,
        fieldValue: String
    ): List<T> {
        val variableName = entityName.uppercase().first()
        return db.execTransaction {
            createQuery(
                "From $entityName $variableName WHERE $variableName.$fieldName = :$fieldName",
                entityType
            )
                .setParameter(fieldName, fieldValue)
                .resultList
        }
    }

    fun add(entity: T) {
        return db.execTransaction {
            save(entity)
        }
    }

    fun update(entity: T): T {
        db.execTransaction {
            update(entity)
        }

        return entity
    }
}