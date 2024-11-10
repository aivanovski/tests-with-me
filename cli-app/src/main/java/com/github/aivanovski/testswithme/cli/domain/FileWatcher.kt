package com.github.aivanovski.testswithme.cli.domain

import java.nio.file.Path

interface FileWatcher {

    fun watch(
        file: Path,
        onContentChanged: (file: Path) -> Unit
    )

    fun cancel()
}