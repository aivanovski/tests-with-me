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
@Table(name = Device.DbFields.TABLE_NAME)
data class Device(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Convert(converter = UidConverter::class)
    @Column(name = DbFields.UID)
    val uid: Uid,

    @Convert(converter = UidConverter::class)
    @Column(name = DbFields.USER_UID)
    val userUid: Uid,

    @Column(name = DbFields.SDK_VERSION)
    val sdkVersion: String,

    @Column(name = DbFields.NAME)
    val name: String,
) {

    object DbFields {
        const val TABLE_NAME = "Devices"

        const val UID = "uid"
        const val USER_UID = "user_uid"
        const val SDK_VERSION = "sdk_version"
        const val NAME = "name"
    }
}