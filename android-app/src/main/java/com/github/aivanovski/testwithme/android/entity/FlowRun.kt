package com.github.aivanovski.testwithme.android.entity

data class FlowRun(
    val uid: String,
    val flowUid: String,
    val userUid: String,
    val finishedAt: Long,
    val isSuccess: Boolean,
    val appVersionName: String,
    val appVersionCode: String
)