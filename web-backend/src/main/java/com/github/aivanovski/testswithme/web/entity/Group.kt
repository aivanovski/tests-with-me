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
@Table(name = Group.DbFields.TABLE_NAME)
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = DbFields.UID)
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = DbFields.PARENT_UID, nullable = true)
    @Convert(converter = UidConverter::class)
    val parentUid: Uid?,

    @Column(name = DbFields.PROJECT_UID)
    @Convert(converter = UidConverter::class)
    val projectUid: Uid,

    @Column(name = DbFields.NAME)
    val name: String
) {
    object DbFields {
        const val TABLE_NAME = "Groups"

        const val UID = "uid"
        const val PARENT_UID = "parent_uid"
        const val PROJECT_UID = "project_uid"
        const val NAME = "name"
    }
}