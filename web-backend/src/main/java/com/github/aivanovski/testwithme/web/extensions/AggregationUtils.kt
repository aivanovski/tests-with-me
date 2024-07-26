package com.github.aivanovski.testwithme.web.extensions

import com.github.aivanovski.testwithme.web.entity.Group

fun List<Group>.aggregateGroupsByParent(): Map<String?, List<Group>> {
    val parentToChildrenMap = HashMap<String?, MutableList<Group>>()

    this.forEach { group ->
        val parentUid = group.parentUid?.toString()

        val children = parentToChildrenMap.getOrDefault(parentUid, mutableListOf())
            .apply {
                add(group)
            }

        parentToChildrenMap[parentUid] = children
    }

    return parentToChildrenMap
}