package com.github.aivanovski.testwithme.android.data.api

import com.github.aivanovski.testwithme.android.entity.FlowRun
import com.github.aivanovski.testwithme.android.entity.Group
import com.github.aivanovski.testwithme.android.entity.SourceType
import com.github.aivanovski.testwithme.android.entity.User
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testwithme.web.api.FlowRunsItemDto
import com.github.aivanovski.testwithme.web.api.FlowsItemDto
import com.github.aivanovski.testwithme.web.api.GroupsItemDto
import com.github.aivanovski.testwithme.web.api.UsersItemDto
import com.github.aivanovski.testwithme.web.api.response.ProjectsItemDto

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

fun List<GroupsItemDto>.toGroups(): List<Group> {
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