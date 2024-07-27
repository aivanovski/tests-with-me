package com.github.aivanovski.testswithme.android.entity

data class Group(
    override val uid: String,
    val parentUid: String?,
    val projectUid: String,
    val name: String
) : Entity