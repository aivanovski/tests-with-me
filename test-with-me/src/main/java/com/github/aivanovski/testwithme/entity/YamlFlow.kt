package com.github.aivanovski.testwithme.entity

data class YamlFlow(
    override val name: String,
    val steps: List<FlowStep>
) : Flow