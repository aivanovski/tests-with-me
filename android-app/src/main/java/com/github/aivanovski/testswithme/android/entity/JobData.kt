package com.github.aivanovski.testswithme.android.entity

import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.android.entity.db.StepEntry

data class JobData(
    val job: JobEntry,
    val flow: FlowWithSteps,
    val currentStep: StepEntry,
    val executionData: LocalStepRun
)