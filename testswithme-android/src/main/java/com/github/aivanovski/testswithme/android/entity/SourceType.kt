package com.github.aivanovski.testswithme.android.entity

enum class SourceType {
    LOCAL,
    REMOTE;

    companion object {
        fun fromName(name: String): SourceType? {
            return values().firstOrNull { type -> type.name == name }
        }
    }
}