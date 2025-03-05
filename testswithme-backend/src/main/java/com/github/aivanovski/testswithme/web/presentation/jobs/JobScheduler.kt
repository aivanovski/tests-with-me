package com.github.aivanovski.testswithme.web.presentation.jobs

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import kotlin.time.Duration.Companion.minutes
import org.quartz.JobBuilder
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory

const val GROUP_UPDATE_FLOWS = "group-update-flows"
const val TRIGGER = "update-flow-trigger"
val UPDATE_FLOWS_REPEAT_INTERVAL = 5.minutes.inWholeMilliseconds

fun Application.configureJobScheduler() {
    val scheduler = StdSchedulerFactory().scheduler
    scheduler.start()

    val job = JobBuilder.newJob(SyncFlowsWithRepositoryJob::class.java)
        .withIdentity(SyncFlowsWithRepositoryJob::class.simpleName, GROUP_UPDATE_FLOWS)
        .build()

    val trigger = TriggerBuilder.newTrigger()
        .withIdentity(TRIGGER, GROUP_UPDATE_FLOWS)
        .startNow()
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule()
                // .withIntervalInMilliseconds(UPDATE_FLOWS_REPEAT_INTERVAL)
                .withIntervalInMinutes(2)
                .repeatForever()
        )
        .build()

    scheduler.scheduleJob(job, trigger)

    environment.monitor.subscribe(ApplicationStopped) {
        scheduler.shutdown()
    }
}