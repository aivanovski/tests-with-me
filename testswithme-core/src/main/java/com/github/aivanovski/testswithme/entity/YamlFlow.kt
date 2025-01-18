package com.github.aivanovski.testswithme.entity

import com.github.aivanovski.testswithme.flow.yaml.model.TextLineRange

data class YamlFlow(
    override val name: String,
    val project: String?,
    val group: String?,
    val steps: List<FlowStep>,
    val stepLines: List<TextLineRange>
) : Flow