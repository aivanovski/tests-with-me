package com.github.aivanovski.testswithme.android.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.aivanovski.testswithme.android.data.db.converters.FlowStepConverter
import com.github.aivanovski.testswithme.android.data.db.dao.FlowEntryDao
import com.github.aivanovski.testswithme.android.data.db.dao.GroupEntryDao
import com.github.aivanovski.testswithme.android.data.db.dao.JobDao
import com.github.aivanovski.testswithme.android.data.db.dao.JobHistoryDao
import com.github.aivanovski.testswithme.android.data.db.dao.LocalStepRunDao
import com.github.aivanovski.testswithme.android.data.db.dao.ProjectEntryDao
import com.github.aivanovski.testswithme.android.data.db.dao.StepEntryDao
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.JobHistoryEntry
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.db.StepEntry

@Database(
    entities = [
        StepEntry::class,
        FlowEntry::class,
        JobEntry::class,
        LocalStepRun::class,
        JobHistoryEntry::class,
        ProjectEntry::class,
        GroupEntry::class
    ],
    version = 1
)
@TypeConverters(
    FlowStepConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract val stepEntryDao: StepEntryDao
    abstract val flowEntryDao: FlowEntryDao
    abstract val runnerEntryDao: JobDao
    abstract val executionDataDao: LocalStepRunDao
    abstract val jobHistoryDao: JobHistoryDao
    abstract val projectEntryDao: ProjectEntryDao
    abstract val groupEntryDao: GroupEntryDao

    companion object {

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "testswithme.db"
            )
                .addTypeConverter(FlowStepConverter())
                .build()
        }
    }
}