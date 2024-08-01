package com.github.aivanovski.testswithme.android.data.api

import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.Group
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.User
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.web.api.FlowRunsItemDto
import com.github.aivanovski.testswithme.web.api.FlowsItemDto
import com.github.aivanovski.testswithme.web.api.GroupItemDto
import com.github.aivanovski.testswithme.web.api.UsersItemDto
import com.github.aivanovski.testswithme.web.api.response.ProjectsItemDto

fun List<FlowsItemDto>.toFlows(): List<FlowEntry> {
    return map { item ->
        FlowEntry(
            id = null,
            uid = item.id,
            projectUid = item.projectId,
            groupUid = item.groupId,
            name = item.name,
            sourceType = SourceType.REMOTE
        )
    }
}

fun List<UsersItemDto>.toUsers(): List<User> {
    return map { item ->
        User(
            uid = item.id,
            name = item.name
        )
    }
}

fun List<GroupItemDto>.toGroups(): List<Group> {
    return map { item ->
        Group(
            uid = item.id,
            name = item.name,
            parentUid = item.parentId,
            projectUid = item.projectId
        )
    }
}

fun List<FlowRunsItemDto>.toFlowRuns(): List<FlowRun> {
    return map { item ->
        FlowRun(
            uid = item.uid,
            flowUid = item.flowUid,
            userUid = item.userUid,
            finishedAt = item.finishedAtTimestamp,
            isSuccess = item.isSuccess,
            appVersionName = item.appVersionName,
            appVersionCode = item.appVersionCode
        )
    }
}

fun List<ProjectsItemDto>.toProjects(): List<ProjectEntry> {
    return map { item ->
        ProjectEntry(
            uid = item.id,
            name = item.name,
            description = item.description.orEmpty(),
            packageName = item.packageName,
            downloadUrl = item.downloadUrl,
            imageUrl = item.imageUrl,
            siteUrl = item.siteUrl
        )
    }
}