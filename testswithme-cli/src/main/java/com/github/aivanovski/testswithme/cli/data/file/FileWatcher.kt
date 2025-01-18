package com.github.aivanovski.testswithme.cli.data.file

import java.nio.file.Path

interface FileWatcher {

    fun watch(file: Path)

    fun cancel()
}