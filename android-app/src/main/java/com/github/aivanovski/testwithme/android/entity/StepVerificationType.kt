package com.github.aivanovski.testwithme.android.entity

enum class StepVerificationType {
    LOCAL,
    REMOTE;

    companion object {

        fun fromName(value: String): StepVerificationType? {
            return StepVerificationType.values()
                .firstOrNull { type -> type.name == value }
        }
    }
}