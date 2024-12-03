package com.github.aivanovski.testswithme.entity

data class YamlFlow(
    override val name: String,
    val project: String?,
    val group: String?,
    val steps: List<FlowStep>
) : Flow