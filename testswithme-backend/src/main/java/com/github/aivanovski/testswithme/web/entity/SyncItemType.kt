package com.github.aivanovski.testswithme.web.entity

enum class SyncItemType {
    INSERT_GROUP,
    INSERT_FLOW,
    UPDATE_FLOW;

    companion object {
        fun getByName(name: String): SyncItemType? =
            entries.firstOrNull { type -> type.name == name }
    }
}