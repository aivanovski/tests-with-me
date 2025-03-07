package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.FlowRun

class FlowRunDao(
    db: AppDatabase
) : Dao<FlowRun>(
    db = db,
    entityType = FlowRun::class.java,
    entityName = FlowRun::class.java.simpleName
)