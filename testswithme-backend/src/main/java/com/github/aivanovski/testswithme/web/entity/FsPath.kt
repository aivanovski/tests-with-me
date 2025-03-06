package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.utils.StringUtils.concatPaths

sealed class FsPath(
    val path: String
) {
    override fun toString(): String = path
}

data class AbsolutePath(
    val basePath: String,
    val relativePath: String
) : FsPath(concatPaths(basePath, relativePath)) {
    override fun toString(): String = path
}

data class RelativePath(
    val relativePath: String
) : FsPath(relativePath) {

    override fun toString(): String = path

    companion object {
        val ROOT = RelativePath(relativePath = "")
    }
}