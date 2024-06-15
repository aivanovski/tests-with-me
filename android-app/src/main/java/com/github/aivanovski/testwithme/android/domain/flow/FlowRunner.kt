package com.github.aivanovski.testwithme.android.domain.flow

import android.view.accessibility.AccessibilityNodeInfo
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.Settings
import com.github.aivanovski.testwithme.android.entity.OnStepFinishedAction
import com.github.aivanovski.testwithme.android.domain.FlowInteractor
import com.github.aivanovski.testwithme.flow.driver.Driver
import com.github.aivanovski.testwithme.android.entity.OnFinishAction
import com.github.aivanovski.testwithme.android.entity.JobStatus
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.entity.db.JobEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.extensions.unwrapError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import arrow.core.Either

class FlowRunner(
    private val settings: Settings,
    private val interactor: FlowInteractor,
    driver: Driver<AccessibilityNodeInfo>
) {

    private val stateRef = AtomicReference(RunnerState.IDLE)
    private val stepIndex = AtomicInteger(0)
    private val jobUidRef = AtomicReference<String?>(null)
    private val listeners = mutableListOf<FlowLifecycleListener>()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main)
    private val commandExecutor = CommandExecutor(interactor, driver)
    private val commandFactory = StepCommandFactory(interactor)

    init {
        listeners.add(TimberFlowReporter())
    }

    fun isRunning(): Boolean = (stateRef.get() == RunnerState.RUNNING)

    fun isIdle(): Boolean = (stateRef.get() == RunnerState.IDLE)

    fun runNextFlow() {
        val jobUid = settings.startJobUid

        scope.launch {
            val findNextJobResult = findNextJobToRun(jobUid)
            if (findNextJobResult.isLeft()) {
                onErrorOccurred(exception = findNextJobResult.unwrapError())
                return@launch
            }

            val nextJobUid = findNextJobResult.getOrNull()
            Timber.d("runNextFlow: jobUid=%s", nextJobUid)

            if (nextJobUid != null) {
                val runResult = runFlow(nextJobUid)
                if (runResult.isLeft()) {
                    onErrorOccurred(exception = runResult.unwrapError())
                    return@launch
                }
            }
        }
    }

    private suspend fun findNextJobToRun(
        targetJobUid: String?
    ): Either<AppException, String?> = either {
        val jobs = interactor.getJobs().bind()

        Timber.d("jobs: size=%s, %s", jobs.size, jobs)

        val running = jobs.filterByStatus(JobStatus.RUNNING)
        val pending = jobs.filterByStatus(JobStatus.PENDING)

        if (running.isNotEmpty() && isIdle()) {
            for (job in running) {
                val cancelledJob = job.copy(
                    status = JobStatus.CANCELLED
                )

                interactor.updateJob(cancelledJob).bind()
            }
        }

        val jobUid = if (pending.isNotEmpty() && isIdle()) {
            val jobToStart = targetJobUid?.let {
                pending.firstOrNull { entry ->
                    entry.uid == targetJobUid
                }
            }

            jobToStart?.uid ?: pending.first().uid
        } else {
            null
        }

        jobUid
    }

    private suspend fun runFlow(
        jobUid: String
    ): Either<AppException, Unit> = either {
        val jobs = interactor.getJobs().bind()

        val job = jobs.firstOrNull { job -> job.uid == jobUid }
            ?: raise(AppException("Failed to find job by uid: $jobUid"))

        val flow = interactor.getFlowByUid(job.flowUid).bind()

        jobUidRef.set(jobUid)
        stateRef.set(RunnerState.RUNNING)
        stepIndex.set(0)

        if (settings.startJobUid == jobUid) {
            settings.startJobUid = null
        }

        val updatedJob = job.copy(status = JobStatus.RUNNING)
        interactor.updateJob(updatedJob).bind()

        notifyOnFlowStarted(flow.entry)

        runCurrentStep(
            initialDelay = 5000L
        ).bind()
    }

    fun stop() {
        stateRef.set(RunnerState.IDLE)
        job.cancel()
    }

    private suspend fun onErrorOccurred(
        jobUid: String? = null,
        exception: AppException
    ) {
        Timber.e("onErrorOccurred: jobUid=%s, error=%s", jobUid, exception)
        Timber.e(exception)
        stateRef.set(RunnerState.IDLE)

        if (jobUid != null) {
            val job = interactor.getJobByUid(jobUid).getOrNull()
            if (job != null) {
                interactor.updateJob(job.copy(status = JobStatus.CANCELLED))
            }
        }
    }

    private suspend fun runCurrentStep(
        initialDelay: Long = DELAY_BETWEEN_STEPS
    ): Either<AppException, Unit> = either {
        delay(initialDelay)

        if (!isRunning()) {
            raise(AppException("Flow was cancelled"))
        }

        val jobData = interactor.getCurrentJobData().bind()

        val (job, flow, stepEntry, executionData) = jobData

        val command = commandFactory.createCommand(stepEntry.command).bind()

        val nextAction = commandExecutor.execute(
            job = job,
            flow = flow.entry,
            stepEntry = stepEntry,
            command = command,
            stepIndex = stepIndex.get(),
            attemptIndex = executionData.attemptCount,
            lifecycleListener = listeners.first()
        ).bind()

        when (nextAction) {
            is OnStepFinishedAction.Next -> {
                stepIndex.incrementAndGet()
                runCurrentStep().bind()
            }

            OnStepFinishedAction.Complete -> {
                finishFlowExecution(
                    jobUid = job.uid,
                    isRunNextAllowed = true
                ).bind()
            }

            OnStepFinishedAction.Retry -> {
                runCurrentStep().bind()
            }

            OnStepFinishedAction.Stop -> {
                finishFlowExecution(
                    jobUid = job.uid,
                    isRunNextAllowed = false
                ).bind()

                raise(AppException("Flow was stopped"))
            }
        }
    }

    private suspend fun finishFlowExecution(
        jobUid: String,
        isRunNextAllowed: Boolean
    ): Either<AppException, Unit> = either {
        stateRef.set(RunnerState.IDLE)

        val job = interactor.getJobByUid(jobUid).bind()

        interactor.removeJob(jobUid).bind()

        val isRunNext = (job.onFinishAction == OnFinishAction.RUN_NEXT)
        if (isRunNext && isRunNextAllowed) {
            scope.launch {
                runNextFlow()
            }
        }
    }

    private fun notifyOnFlowStarted(
        flow: FlowEntry
    ) {
        for (listener in listeners) {
            listener.onFlowStarted(flow)
        }
    }

    // private fun notifyOnFlowFinished(
    //     flow: FlowEntry,
    //     result: Either<AppException, Any>
    // ) {
    //     for (listener in listeners) {
    //         listener.onFlowFinished(flow, result)
    //     }
    // }

    // private fun notifyOnStepStarted(
    //     flow: FlowEntry,
    //     command: StepCommand,
    //     stepIndex: Int,
    //     attemptIndex: Int
    // ) {
    //     for (listener in listeners) {
    //         listener.onStepStarted(flow, command, stepIndex, attemptIndex)
    //     }
    // }

    // private fun notifyOnStepFinished(
    //     flow: FlowEntry,
    //     command: StepCommand,
    //     stepIndex: Int,
    //     result: Either<AppException, Any>
    // ) {
    //     for (listener in listeners) {
    //         listener.onStepFinished(flow, command, stepIndex, result)
    //     }
    // }

    private fun List<JobEntry>.filterByStatus(
        status: JobStatus
    ): List<JobEntry> {
        return filter { job -> job.status == status }
    }

    enum class RunnerState {
        IDLE,
        RUNNING
    }

    companion object {
        private const val DELAY_IF_PENDING_START = 5000L // in milliseconds
        const val DELAY_BETWEEN_STEPS = 1000L // in milliseconds
    }
}