package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.Flow

class FlowDao(
    db: AppDatabase
) : Dao<Flow>(
    db = db,
    entityType = Flow::class.java,
    entityName = "Flow"
)