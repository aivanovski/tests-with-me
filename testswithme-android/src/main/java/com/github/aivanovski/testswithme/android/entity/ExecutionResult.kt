package com.github.aivanovski.testswithme.android.entity

enum class ExecutionResult {
    NONE,
    SUCCESS,
    FAILED;

    companion object {
        fun fromName(name: String): ExecutionResult? {
            return values().firstOrNull { status -> status.name == name }
        }
    }
}