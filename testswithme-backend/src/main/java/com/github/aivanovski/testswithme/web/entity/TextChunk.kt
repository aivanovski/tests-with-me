package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testswithme.web.entity.TextChunk.DbFields.CHUNK_INDEX
import com.github.aivanovski.testswithme.web.entity.TextChunk.DbFields.CHUNK_SIZE_IN_BYTES
import com.github.aivanovski.testswithme.web.entity.TextChunk.DbFields.CONTENT
import com.github.aivanovski.testswithme.web.entity.TextChunk.DbFields.ENTITY_UID
import com.github.aivanovski.testswithme.web.entity.TextChunk.DbFields.TABLE_NAME
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = TABLE_NAME)
data class TextChunk(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0L,

    @Column(name = ENTITY_UID)
    @Convert(converter = UidConverter::class)
    val entityUid: Uid,

    @Column(name = CHUNK_INDEX)
    val chunkIndex: Int,

    @Column(name = CONTENT, length = CHUNK_SIZE_IN_BYTES)
    val content: String
) {
    object DbFields {
        const val TABLE_NAME = "TextChunks"

        const val ENTITY_UID = "entity_uid"
        const val CHUNK_INDEX = "chunk_index"
        const val CONTENT = "content"

        const val CHUNK_SIZE_IN_BYTES = 1024
    }
}