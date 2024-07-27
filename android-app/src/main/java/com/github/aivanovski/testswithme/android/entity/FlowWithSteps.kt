package com.github.aivanovski.testswithme.android.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.StepEntry

data class FlowWithSteps(
    @Embedded
    val entry: FlowEntry,

    @Relation(
        parentColumn = "uid",
        entityColumn = "flow_uid"
    )
    val steps: List<StepEntry>
)