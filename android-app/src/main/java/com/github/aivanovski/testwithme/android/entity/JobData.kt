package com.github.aivanovski.testwithme.android.entity

import com.github.aivanovski.testwithme.android.entity.db.ExecutionData
import com.github.aivanovski.testwithme.android.entity.db.JobEntry
import com.github.aivanovski.testwithme.android.entity.db.StepEntry

data class JobData(
    val job: JobEntry,
    val flow: FlowWithSteps,
    val currentStep: StepEntry,
    val executionData: ExecutionData
)