package com.github.aivanovski.testwithme.android.entity

data class Group(
    override val uid: String,
    val parentUid: String?,
    val projectUid: String,
    val name: String
) : Entity