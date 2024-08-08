package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

// TODO: improve security, password should be hashed and salted

@Entity(name = "User_") // TODO: specify
@Table(name = User.DbFields.TABLE_NAME)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = DbFields.UID)
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = DbFields.NAME)
    val name: String,

    @Column(name = DbFields.EMAIL)
    val email: String,

    @Column(name = DbFields.PASSWORD)
    val password: String
) {

    object DbFields {
        const val TABLE_NAME = "Users"

        const val UID = "uid"
        const val NAME = "name"
        const val EMAIL = "email"
        const val PASSWORD = "password"
    }
}