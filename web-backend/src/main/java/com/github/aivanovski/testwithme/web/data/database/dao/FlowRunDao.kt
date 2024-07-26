package com.github.aivanovski.testwithme.web.data.database.dao

import com.github.aivanovski.testwithme.web.data.database.AppDatabase
import com.github.aivanovski.testwithme.web.entity.FlowRun

class FlowRunDao(
    db: AppDatabase
) : Dao<FlowRun>(
    db = db,
    entityType = FlowRun::class.java,
    entityName = "FlowRun"
)