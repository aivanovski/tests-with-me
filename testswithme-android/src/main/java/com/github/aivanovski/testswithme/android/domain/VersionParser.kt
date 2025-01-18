package com.github.aivanovski.testswithme.android.domain

import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.extensions.toIntSafely

class VersionParser {

    fun parseVersions(
        versionName: String,
        versionCode: String
    ): AppVersion {
        return AppVersion(
            code = versionCode.toIntSafely() ?: 0,
            name = versionName
        )
    }
}