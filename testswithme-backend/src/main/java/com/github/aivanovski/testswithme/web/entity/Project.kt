package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.DESCRIPTION
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.DOWNLOAD_URL
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.IMAGE_URL
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.IS_DELETED
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.NAME
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.PACKAGE_NAME
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.ROOT_GROUP_UID
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.SITE_URL
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.TABLE_NAME
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.TEST_SOURCE_UID
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.UID
import com.github.aivanovski.testswithme.web.entity.Project.DbFields.USER_UID
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = TABLE_NAME)
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = UID)
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = USER_UID)
    @Convert(converter = UidConverter::class)
    val userUid: Uid,

    @Column(name = ROOT_GROUP_UID)
    @Convert(converter = UidConverter::class)
    val rootGroupUid: Uid,

    @Column(name = TEST_SOURCE_UID, nullable = true)
    @Convert(converter = UidConverter::class)
    val testSourceUid: Uid?,

    @Column(name = PACKAGE_NAME)
    val packageName: String,

    @Column(name = NAME)
    val name: String,

    @Column(name = DESCRIPTION, nullable = true)
    val description: String?,

    @Column(name = DOWNLOAD_URL)
    val downloadUrl: String,

    @Column(name = IMAGE_URL, nullable = true)
    val imageUrl: String?,

    @Column(name = SITE_URL, nullable = true)
    val siteUrl: String?,

    @Column(name = IS_DELETED)
    val isDeleted: Boolean
) {
    object DbFields {
        const val TABLE_NAME = "Projects"

        const val UID = "uid"
        const val USER_UID = "user_uid"
        const val ROOT_GROUP_UID = "root_group_uid"
        const val TEST_SOURCE_UID = "test_source_uid"
        const val PACKAGE_NAME = "package_name"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val DOWNLOAD_URL = "download_url"
        const val IMAGE_URL = "image_url"
        const val SITE_URL = "site_url"
        const val IS_DELETED = "is_deleted"
    }
}