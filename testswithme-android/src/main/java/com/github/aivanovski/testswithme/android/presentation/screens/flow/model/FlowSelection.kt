package com.github.aivanovski.testswithme.android.presentation.screens.flow.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface FlowSelection {

    @Serializable
    data object Remained : FlowSelection

    @Serializable
    data object Failed : FlowSelection

    @Serializable
    data object Passed : FlowSelection
}