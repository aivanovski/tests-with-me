package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.database.dao.ProcessedSyncItemDao
import com.github.aivanovski.testswithme.web.data.database.dao.SyncResultDao
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem
import com.github.aivanovski.testswithme.web.entity.SyncResult
import com.github.aivanovski.testswithme.web.entity.SyncResultWithItems
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException

class SyncResultRepository(
    private val syncResultDao: SyncResultDao,
    private val syncItemDao: ProcessedSyncItemDao
) {

    fun getAll(): Either<AppException, List<SyncResult>> =
        either {
            syncResultDao.getAll()
        }

    fun getAllWithItems(): Either<AppException, List<SyncResultWithItems>> =
        either {
            val results = syncResultDao.getAll()
            val items = syncItemDao.getAll()

            val syncUidToItemsMap = items.groupBy { item -> item.syncUid }

            results.map { result ->
                SyncResultWithItems(
                    result = result,
                    items = syncUidToItemsMap[result.uid] ?: emptyList()
                )
            }
        }

    fun getByUid(uid: Uid): Either<AppException, SyncResult> =
        either {
            syncResultDao.findByUid(uid)
                ?: raise(EntityNotFoundByUidException(SyncResult::class, uid))
        }

    fun add(
        result: SyncResult,
        items: List<ProcessedSyncItem>
    ): Either<AppException, SyncResult> =
        either {
            syncResultDao.add(result)
            syncItemDao.add(items)

            getByUid(result.uid).bind()
        }
}