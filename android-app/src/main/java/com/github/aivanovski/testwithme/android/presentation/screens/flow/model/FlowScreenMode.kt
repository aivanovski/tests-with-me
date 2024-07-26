package com.github.aivanovski.testwithme.android.presentation.screens.flow.model

import com.github.aivanovski.testwithme.android.entity.AppVersion
import kotlinx.serialization.Serializable

@Serializable
sealed class FlowScreenMode {

    @Serializable
    data class Flow(
        val flowUid: String,
        val requiredVersion: AppVersion? = null
    ) : FlowScreenMode()

    @Serializable
    data class Group(
        val groupUid: String
    ) : FlowScreenMode()

    @Serializable
    data class RemainedFlows(
        val projectUid: String,
        val version: AppVersion?
    ) : FlowScreenMode()
}