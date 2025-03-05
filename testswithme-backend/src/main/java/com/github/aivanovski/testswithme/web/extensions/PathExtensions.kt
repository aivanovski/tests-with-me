package com.github.aivanovski.testswithme.web.extensions

import com.github.aivanovski.testswithme.web.entity.AbsolutePath
import com.github.aivanovski.testswithme.web.entity.RelativePath
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

fun AbsolutePath.isDirectory() =
    Files.isDirectory(Path(path))

fun AbsolutePath.toPath(): Path =
    Path(path)

fun AbsolutePath.toFile(): File =
    toPath().toFile()

fun AbsolutePath.getName(): String =
    toPath().name

fun AbsolutePath.toRelative(): RelativePath =
    RelativePath(
        relativePath = relativePath
    )

fun String.asRelativePath(): RelativePath =
    RelativePath(this)