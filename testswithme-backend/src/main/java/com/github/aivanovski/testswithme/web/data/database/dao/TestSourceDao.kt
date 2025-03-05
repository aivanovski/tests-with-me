package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.TestSource

class TestSourceDao(
    db: AppDatabase
) : Dao<TestSource>(
    db = db,
    entityType = TestSource::class.java,
    entityName = TestSource::class.java.simpleName
)