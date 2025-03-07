package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testswithme.web.entity.Group.DbFields.IS_DELETED
import com.github.aivanovski.testswithme.web.entity.Group.DbFields.NAME
import com.github.aivanovski.testswithme.web.entity.Group.DbFields.PARENT_UID
import com.github.aivanovski.testswithme.web.entity.Group.DbFields.PROJECT_UID
import com.github.aivanovski.testswithme.web.entity.Group.DbFields.TABLE_NAME
import com.github.aivanovski.testswithme.web.entity.Group.DbFields.UID
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = TABLE_NAME)
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = UID)
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = PARENT_UID, nullable = true)
    @Convert(converter = UidConverter::class)
    val parentUid: Uid?,

    @Column(name = PROJECT_UID)
    @Convert(converter = UidConverter::class)
    val projectUid: Uid,

    @Column(name = NAME)
    val name: String,

    @Column(name = IS_DELETED)
    val isDeleted: Boolean
) {
    object DbFields {
        const val TABLE_NAME = "Groups"

        const val UID = "uid"
        const val PARENT_UID = "parent_uid"
        const val PROJECT_UID = "project_uid"
        const val NAME = "name"
        const val IS_DELETED = "is_deleted"
    }
}