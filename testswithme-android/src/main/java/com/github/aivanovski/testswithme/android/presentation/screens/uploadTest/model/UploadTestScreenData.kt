package com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model

import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry

data class UploadTestScreenData(
    val projects: List<ProjectEntry>,
    val groups: List<GroupEntry>,
    val content: String,
    val base64Content: String
)