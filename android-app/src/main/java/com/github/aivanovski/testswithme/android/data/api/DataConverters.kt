package com.github.aivanovski.testswithme.android.data.api

import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.User
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.entity.Hash
import com.github.aivanovski.testswithme.entity.HashType
import com.github.aivanovski.testswithme.web.api.FlowRunsItemDto
import com.github.aivanovski.testswithme.web.api.FlowsItemDto
import com.github.aivanovski.testswithme.web.api.GroupItemDto
import com.github.aivanovski.testswithme.web.api.Sha256HashDto
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
            sourceType = SourceType.REMOTE,
            contentHash = item.contentHash.toHash()
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

fun List<GroupItemDto>.toGroups(): List<GroupEntry> {
    return map { item ->
        GroupEntry(
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
            uid = item.id,
            flowUid = item.flowId,
            userUid = item.userId,
            finishedAt = item.finishedAtTimestamp,
            isSuccess = item.isSuccess,
            appVersionName = item.appVersionName,
            appVersionCode = item.appVersionCode,
            isExpired = item.isExpired
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

fun Sha256HashDto.toHash(): Hash =
    Hash(
        type = HashType.SHA_256,
        value = this.value
    )