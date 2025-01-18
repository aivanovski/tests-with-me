package com.github.aivanovski.testswithme.android.entity

sealed class OnStepFinishedAction {
    data class Next(val nextStepUid: String) : OnStepFinishedAction()
    data object Complete : OnStepFinishedAction()
    data object Retry : OnStepFinishedAction()
    data object Stop : OnStepFinishedAction()
}