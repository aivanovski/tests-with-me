package com.github.aivanovski.testwithme.web.entity

import com.github.aivanovski.testwithme.web.data.database.converters.UidConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

// TODO: improve security, password should be encrypted and salted

@Entity(name = "User_") // TODO: specify
@Table(name = "Users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = "uid")
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = "name")
    val name: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "password")
    val password: String
)