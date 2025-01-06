package com.github.aivanovski.testswithme.android.entity

import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry

data class FlowRunWithReport(
    val run: FlowRunEntry,
    val report: String
)