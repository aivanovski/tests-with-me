package com.github.aivanovski.testwithme.android.domain.dataconverters

import com.github.aivanovski.testwithme.android.entity.SourceType
import com.github.aivanovski.testwithme.android.entity.StepVerificationType
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.entity.db.StepEntry
import com.github.aivanovski.testwithme.entity.FlowStep
import com.github.aivanovski.testwithme.entity.YamlFlow

fun YamlFlow.convertToFlowEntry(
    flowUid: String,
    projectUid: String,
    sourceType: SourceType
): FlowEntry {
    return FlowEntry(
        id = null,
        uid = flowUid,
        projectUid = projectUid,
        groupUid = null,
        name = name,
        sourceType = sourceType
    )
}

fun List<FlowStep>.convertToStepEntries(flowUid: String): List<StepEntry> {
    val steps = mutableListOf<StepEntry>()
    for (stepIdx in this.indices) {
        val step = this[stepIdx]
        val nextStep = this.getOrNull(stepIdx + 1)
        val stepUid = "$flowUid:$stepIdx"

        val nextUid = if (nextStep != null) {
            "$flowUid:${stepIdx + 1}"
        } else {
            null
        }

        steps.add(
            StepEntry(
                id = null,
                uid = stepUid,
                index = stepIdx,
                flowUid = flowUid,
                nextUid = nextUid,
                command = step,
                stepVerificationType = StepVerificationType.LOCAL
            )
        )
    }

    return steps
}