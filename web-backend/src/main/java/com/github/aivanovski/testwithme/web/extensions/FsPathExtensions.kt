package com.github.aivanovski.testwithme.web.extensions

import com.github.aivanovski.testwithme.web.entity.FsPath
import java.nio.file.Path
import kotlin.io.path.Path

fun FsPath.toPath(base: String): Path {
    return Path(base, this.path)
}