package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.TextChunk
import com.github.aivanovski.testswithme.web.entity.Uid

class TextChunkDao(
    db: AppDatabase
) : Dao<TextChunk>(
    db = db,
    entityType = TextChunk::class.java,
    entityName = "TextChunk"
) {

    fun getByEntityUid(entityUid: Uid): List<TextChunk> {
        val variableName = entityName.first().uppercase()
        return db.execTransaction {
            createQuery(
                "From $entityName $variableName WHERE $variableName.entityUid = :uid",
                entityType
            )
                .setParameter("uid", entityUid)
                .resultList
        }
    }
}