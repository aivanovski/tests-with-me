package com.github.aivanovski.testswithme.android.data.repository

inline fun <T> mergeEntities(
    localEntities: List<T>,
    remoteEntities: List<T>,
    entityToUidMapper: (T) -> String,
    onInsert: (T) -> Unit,
    onUpdate: (local: T, remote: T) -> Unit,
    onDelete: (T) -> Unit
) {
    val uidToLocalEntityMap = localEntities
        .associateBy { entity -> entityToUidMapper.invoke(entity) }
        .toMutableMap()

    for (remote in remoteEntities) {
        val uid = entityToUidMapper.invoke(remote)
        val local = uidToLocalEntityMap.remove(uid)
        if (local != null) {
            onUpdate.invoke(local, remote)
        } else {
            onInsert.invoke(remote)
        }
    }

    for (entity in uidToLocalEntityMap.values) {
        onDelete.invoke(entity)
    }
}