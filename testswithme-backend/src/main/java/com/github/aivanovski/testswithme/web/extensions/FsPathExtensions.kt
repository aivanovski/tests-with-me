package com.github.aivanovski.testswithme.web.extensions

import com.github.aivanovski.testswithme.web.entity.FsPath
import java.nio.file.Path
import kotlin.io.path.Path

fun FsPath.toPath(base: String): Path {
    return Path(base, this.path)
}