package com.github.aivanovski.testwithme.android.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StartArgs(
    val flowUid: String?,
    val isShowTestRuns: Boolean
) : Parcelable {

    companion object {
        val EMPTY = StartArgs(
            flowUid = null,
            isShowTestRuns = false
        )
    }
}