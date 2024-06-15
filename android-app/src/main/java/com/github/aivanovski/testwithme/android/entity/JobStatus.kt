package com.github.aivanovski.testwithme.android.entity

enum class JobStatus {
    PENDING,
    RUNNING,
    CANCELLED;

    companion object {
        fun fromName(name: String): JobStatus? {
            return values().firstOrNull { status -> status.name == name }
        }
    }
}