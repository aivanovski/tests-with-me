package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.SyncResult

class SyncResultDao(
    db: AppDatabase
) : Dao<SyncResult>(
    db = db,
    entityType = SyncResult::class.java,
    entityName = SyncResult::class.java.simpleName
)