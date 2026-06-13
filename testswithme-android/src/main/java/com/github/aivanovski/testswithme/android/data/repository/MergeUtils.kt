package com.github.aivanovski.testswithme.android.data.repository

import timber.log.Timber

inline fun <T : Any> mergeEntities(
    onLoadLocalEntities: () -> List<T>,
    remoteEntities: List<T>,
    entityToUidMapper: (T) -> String,
    onInsert: (T) -> Unit,
    onUpdate: (local: T, remote: T) -> Unit,
    onDelete: (T) -> Unit
) {
    val uidToLocalEntityMap = onLoadLocalEntities.invoke()
        .associateBy { entity -> entityToUidMapper.invoke(entity) }
        .toMutableMap()

    for (remote in remoteEntities) {
        val uid = entityToUidMapper.invoke(remote)
        val local = uidToLocalEntityMap.remove(uid)
        if (local != null) {
            Timber.d("Update entity ${local::class.java.simpleName}: uid=$uid")
            onUpdate.invoke(local, remote)
        } else {
            Timber.d("Insert entity ${remote::class.java.simpleName}: uid=$uid")
            onInsert.invoke(remote)
        }
    }

    for (entity in uidToLocalEntityMap.values) {
        val uid = entityToUidMapper.invoke(entity)
        Timber.d("Delete entity ${entity::class.java.simpleName}: uid=$uid")
        onDelete.invoke(entity)
    }
}