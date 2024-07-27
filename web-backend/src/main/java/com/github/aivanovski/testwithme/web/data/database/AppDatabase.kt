package com.github.aivanovski.testwithme.web.data.database

import com.github.aivanovski.testwithme.web.data.database.converters.FsPathConverter
import com.github.aivanovski.testwithme.web.data.database.converters.TimestampConverter
import com.github.aivanovski.testwithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testwithme.web.di.GlobalInjector.get
import com.github.aivanovski.testwithme.web.entity.Flow
import com.github.aivanovski.testwithme.web.entity.FlowRun
import com.github.aivanovski.testwithme.web.entity.Group
import com.github.aivanovski.testwithme.web.entity.Project
import com.github.aivanovski.testwithme.web.entity.User
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

                    // Converters
                    addAttributeConverter(FsPathConverter::class.java, true)
                    addAttributeConverter(UidConverter::class.java, true)
                    addAttributeConverter(TimestampConverter::class.java, true)
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