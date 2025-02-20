package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "TextChunks")
data class TextChunk(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0L,

    @Column(name = "entity_uid")
    @Convert(converter = UidConverter::class)
    val entityUid: Uid,

    @Column(name = "chunk_index")
    val chunkIndex: Int,

    @Column(name = "content", length = 1024)
    val content: String
)