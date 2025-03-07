package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testswithme.web.entity.User.DbFields.EMAIL
import com.github.aivanovski.testswithme.web.entity.User.DbFields.NAME
import com.github.aivanovski.testswithme.web.entity.User.DbFields.PASSWORD
import com.github.aivanovski.testswithme.web.entity.User.DbFields.TABLE_NAME
import com.github.aivanovski.testswithme.web.entity.User.DbFields.UID
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

// TODO: improve security, password should be hashed and salted

@Entity
@Table(name = TABLE_NAME)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = UID)
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = NAME)
    val name: String,

    @Column(name = EMAIL)
    val email: String,

    @Column(name = PASSWORD)
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