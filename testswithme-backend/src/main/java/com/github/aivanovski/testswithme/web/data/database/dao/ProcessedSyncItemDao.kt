package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem

class ProcessedSyncItemDao(
    db: AppDatabase
) : Dao<ProcessedSyncItem>(
    db = db,
    entityType = ProcessedSyncItem::class.java,
    entityName = ProcessedSyncItem::class.java.simpleName
)