package com.github.aivanovski.testwithme.android.entity

enum class FlowStatus {
    NONE,
    SUCCESS,
    FAILED;

    companion object {
        fun fromName(name: String): FlowStatus? {
            return values().firstOrNull { status -> status.name == name }
        }
    }
}