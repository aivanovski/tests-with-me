package com.github.aivanovski.testswithme.web.data.database

import com.github.aivanovski.testswithme.web.data.database.converters.HashConverter
import com.github.aivanovski.testswithme.web.data.database.converters.SyncItemTypeConverter
import com.github.aivanovski.testswithme.web.data.database.converters.TimestampConverter
import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.FlowRun
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.SyncResult
import com.github.aivanovski.testswithme.web.entity.TextChunk
import com.github.aivanovski.testswithme.web.entity.TestSource
import com.github.aivanovski.testswithme.web.entity.User
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class AppDatabase {

    private val sessionFactory = buildSessionFactory()

    private fun buildSessionFactory(): SessionFactory {
        try {
            val configuration = Configuration()
                .apply {
                    // Database entities
                    addAnnotatedClass(User::class.java)
                    addAnnotatedClass(Project::class.java)
                    addAnnotatedClass(Group::class.java)
                    addAnnotatedClass(Flow::class.java)
                    addAnnotatedClass(FlowRun::class.java)
                    addAnnotatedClass(TextChunk::class.java)
                    addAnnotatedClass(TestSource::class.java)
                    addAnnotatedClass(SyncResult::class.java)
                    addAnnotatedClass(ProcessedSyncItem::class.java)

                    // Converters
                    addAttributeConverter(UidConverter::class.java, true)
                    addAttributeConverter(TimestampConverter::class.java, true)
                    addAttributeConverter(HashConverter::class.java, true)
                    addAttributeConverter(SyncItemTypeConverter::class.java, true)
                }
                .configure()

            val serviceRegistry = StandardServiceRegistryBuilder()
                .applySettings(configuration.properties)
                .build()

            return configuration.buildSessionFactory(serviceRegistry)
        } catch (ex: Exception) {
            throw ExceptionInInitializerError(ex)
        }
    }

    fun <T> execTransaction(block: Session.() -> T): T {
        return sessionFactory.openSession().use { session ->
            val transaction = session.beginTransaction()
            val result = block.invoke(session)
            transaction.commit()
            result
        }
    }
}

fun configureDatabase() {
    val db: AppDatabase = get()
}