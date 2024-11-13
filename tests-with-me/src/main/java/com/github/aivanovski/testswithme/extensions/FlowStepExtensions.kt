package com.github.aivanovski.testswithme.extensions

import com.github.aivanovski.testswithme.entity.FlowStep

fun FlowStep.isStepFlaky(): Boolean {
    return this is FlowStep.AssertVisible ||
        this is FlowStep.AssertNotVisible ||
        this is FlowStep.TapOn
}