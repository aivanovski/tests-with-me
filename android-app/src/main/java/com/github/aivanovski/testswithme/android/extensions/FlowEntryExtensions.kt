package com.github.aivanovski.testswithme.android.extensions

import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry

fun List<FlowEntry>.filterByGroupUid(groupUid: String): List<FlowEntry> =
    filter { flow -> flow.groupUid == groupUid }

fun List<FlowEntry>.filterRemoteOnly(): List<FlowEntry> =
    filter { flow -> flow.sourceType == SourceType.REMOTE }