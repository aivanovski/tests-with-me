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
@Table(name = "Projects")
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = "uid")
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = "user_uid")
    @Convert(converter = UidConverter::class)
    val userUid: Uid,

    @Column(name = "root_group_uid")
    @Convert(converter = UidConverter::class)
    val rootGroupUid: Uid,

    @Column(name = "package_name")
    val packageName: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "description", nullable = true)
    val description: String?,

    @Column(name = "download_url")
    val downloadUrl: String,

    @Column(name = "image_url", nullable = true)
    val imageUrl: String?,

    @Column(name = "site_url", nullable = true)
    val siteUrl: String?,

    @Column(name = "is_deleted")
    val isDeleted: Boolean
) {
    // TODO: add db fields
}