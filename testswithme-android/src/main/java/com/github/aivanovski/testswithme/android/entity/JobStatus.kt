package com.github.aivanovski.testswithme.android.entity

enum class JobStatus {
    PENDING,
    RUNNING,
    CANCELLED,
    FINISHED;

    companion object {
        fun fromName(name: String): JobStatus? {
            return values().firstOrNull { status -> status.name == name }
        }
    }
}