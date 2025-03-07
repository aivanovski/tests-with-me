package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.entity.Hash
import com.github.aivanovski.testswithme.web.data.database.converters.HashConverter
import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testswithme.web.entity.Flow.DbFields.CONTENT_HASH
import com.github.aivanovski.testswithme.web.entity.Flow.DbFields.GROUP_UID
import com.github.aivanovski.testswithme.web.entity.Flow.DbFields.IS_DELETED
import com.github.aivanovski.testswithme.web.entity.Flow.DbFields.NAME
import com.github.aivanovski.testswithme.web.entity.Flow.DbFields.PROJECT_UID
import com.github.aivanovski.testswithme.web.entity.Flow.DbFields.TABLE_NAME
import com.github.aivanovski.testswithme.web.entity.Flow.DbFields.UID
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = TABLE_NAME)
data class Flow(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = UID)
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = PROJECT_UID)
    @Convert(converter = UidConverter::class)
    val projectUid: Uid,

    @Column(name = GROUP_UID, nullable = true)
    @Convert(converter = UidConverter::class)
    val groupUid: Uid,

    @Column(name = NAME)
    val name: String,

    @Column(name = CONTENT_HASH)
    @Convert(converter = HashConverter::class)
    val contentHash: Hash,

    @Column(name = IS_DELETED)
    val isDeleted: Boolean
) {
    object DbFields {
        const val TABLE_NAME = "Flows"

        const val UID = "uid"
        const val PROJECT_UID = "project_uid"
        const val GROUP_UID = "group_uid"
        const val NAME = "name"
        const val CONTENT_HASH = "content_hash"
        const val IS_DELETED = "is_deleted"
    }
}