package com.github.aivanovski.testswithme.flow.yaml.model

data class TextLineRange(
    val start: Int,
    val end: Int,
    val lines: List<TextLine>
)