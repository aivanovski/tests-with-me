package com.github.aivanovski.testswithme.web.utils

import com.github.aivanovski.testswithme.utils.StringUtils

sealed interface FileEntity {

    val path: String

    data class Directory(
        override val path: String
    ) : FileEntity

    data class File(
        override val path: String,
        val content: String = StringUtils.EMPTY
    ) : FileEntity
}