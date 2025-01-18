package com.github.aivanovski.testswithme.cli.presentation.main.model

import com.github.aivanovski.testswithme.entity.YamlFlow
import java.nio.file.Path

data class TestData(
    val jobId: String,
    val file: Path,
    val content: String,
    val flow: YamlFlow
)