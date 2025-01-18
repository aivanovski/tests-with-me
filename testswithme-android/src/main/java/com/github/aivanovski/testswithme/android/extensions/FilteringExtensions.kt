package com.github.aivanovski.testswithme.android.extensions

import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry

fun List<FlowEntry>.filterByGroupUid(groupUid: String): List<FlowEntry> =
    filter { flow -> flow.groupUid == groupUid }

fun List<FlowEntry>.filterRemoteOnly(): List<FlowEntry> =
    filter { flow -> flow.sourceType == SourceType.REMOTE }

fun List<FlowEntry>.filterBySourceType(sourceType: SourceType): List<FlowEntry> =
    filter { flow -> flow.sourceType == sourceType }

fun List<FlowEntry>.filterByProjectUid(projectUid: String): List<FlowEntry> =
    filter { flow -> flow.projectUid == projectUid }

fun List<GroupEntry>.filterGroupsByProjectUid(projectUid: String): List<GroupEntry> =
    filter { group -> group.projectUid == projectUid }