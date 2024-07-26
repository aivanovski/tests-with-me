package com.github.aivanovski.testwithme.android.domain

import com.github.aivanovski.testwithme.android.entity.AppVersion
import com.github.aivanovski.testwithme.extensions.toIntSafely

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