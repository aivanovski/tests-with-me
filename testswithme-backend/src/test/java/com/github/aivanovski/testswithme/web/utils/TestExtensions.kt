package com.github.aivanovski.testswithme.web.utils

import com.github.aivanovski.testswithme.web.entity.Uid
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes

fun String.asUid(): Uid =
    Uid(this)

fun List<FileEntity>.setupFiles(rootDir: Path) {
    val directories = this.mapNotNull { entity -> entity as? FileEntity.Directory }
    val files = this.mapNotNull { entity -> entity as? FileEntity.File }

    directories.forEach { dir ->
        Paths.get(rootDir.toString() + "/" + dir.path).createDirectories()
    }

    files.forEach { file ->
        Paths.get(rootDir.toString() + "/" + file.path).writeBytes(file.content.toByteArray())
    }
}