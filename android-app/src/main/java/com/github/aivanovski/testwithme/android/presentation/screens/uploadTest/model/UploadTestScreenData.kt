package com.github.aivanovski.testwithme.android.presentation.screens.uploadTest.model

import com.github.aivanovski.testwithme.android.entity.Group
import com.github.aivanovski.testwithme.android.entity.db.ProjectEntry

data class UploadTestScreenData(
    val projects: List<ProjectEntry>,
    val groups: List<Group>,
    val content: String,
    val base64Content: String
)