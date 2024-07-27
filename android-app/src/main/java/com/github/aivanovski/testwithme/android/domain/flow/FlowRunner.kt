package com.github.aivanovski.testwithme.android.domain.flow

import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityNodeInfo
import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.settings.Settings
import com.github.aivanovski.testwithme.android.domain.flow.logger.TimberLogger
import com.github.aivanovski.testwithme.android.domain.flow.reporter.TimberFlowReporter
import com.github.aivanovski.testwithme.android.entity.JobStatus
import com.github.aivanovski.testwithme.android.entity.OnFinishAction
import com.github.aivanovski.testwithme.android.entity.OnStepFinishedAction
import com.github.aivanovski.testwithme.android.entity.SourceType
import com.github.aivanovski.testwithme.android.entity.db.JobEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.presentation.MainActivity
import com.github.aivanovski.testwithme.android.presentation.StartArgs
import com.github.aivanovski.testwithme.extensions.unwrap
import com.github.aivanovski.testwithme.extensions.unwrapError
import com.github.aivanovski.testwithme.flow.commands.StepCommand
import com.github.aivanovski.testwithme.flow.driver.Driver
import com.github.aivanovski.testwithme.flow.runner.ExecutionContext
import com.github.aivanovski.testwithme.flow.runner.listener.ListenerComposite
import com.github.aivanovski.testwithme.flow.runner.reporter.ReportCollector
import com.github.aivanovski.testwithme.flow.runner.reporter.TimeCollector
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class FlowRunner(
    private val context: Context,
    private val settings: Settings,
    private val interactor: FlowRunnerInteractor,
    driver: Driver<AccessibilityNodeInfo>
) {

    private val logger = TimberLogger(tag = TimberFlowReporter::class.java.simpleName)
    private val stateRef = AtomicReference(RunnerState.IDLE)
    private val stepIndex = AtomicInteger(0)
    private val jobUidRef = AtomicReference<String?>(null)
    private val listeners = ListenerComposite()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main)
    private val executionContext = ExecutionContext(
        driver = driver,
        logger = logger
    )
    private val commandExecutor = CommandExecutor(interactor, executionContext, listeners)
    private val commandFactory = StepCommandFactory(interactor)
    private val standaloneCommands = ConcurrentLinkedQueue<StepCommand>()
    private val timeCollector = TimeCollector()
    private val reportCollector = ReportCollector()

    init {
        listeners.add(TimberFlowReporter(logger))
        listeners.add(timeCollector)
        listeners.add(reportCollector)
    }

    fun isRunning(): Boolean = (stateRef.get() == RunnerState.RUNNING)

    fun isIdle(): Boolean = (stateRef.get() == RunnerState.IDLE)

    fun runOrAddToQueue(command: StepCommand) {
        standaloneCommands.add(command)

        scope.launch {
            runStandAloneCommandsIfNeed()
        }
    }

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

                runStandAloneCommandsIfNeed()
            }
        }
    }

    private suspend fun findNextJobToRun(targetJobUid: String?): Either<AppException, String?> =
        either {
            val jobs = interactor.getJobs()
                .bind()
                .sortedByDescending { job -> job.addedTimestamp }

            val running = jobs.filterByStatus(JobStatus.RUNNING)
            val pending = jobs.filterByStatus(JobStatus.PENDING)

            val cancelledUids = jobs
                .filterByStatus(JobStatus.CANCELLED)
                .map { job -> job.uid }

            val finishedUids = jobs
                .filterByStatus(JobStatus.FINISHED)
                .map { job -> job.uid }

            // TODO: try to upload FINISHED jobs and then move them to the history

            if (targetJobUid in cancelledUids || targetJobUid in finishedUids) {
                settings.startJobUid = null
            }

            Timber.d(
                "jobs: size=%s, running=%s, pending=%s, targetJob=%s",
                jobs.size,
                running.size,
                pending.size,
                targetJobUid
            )
            for (job in jobs) {
                Timber.d("    %s", job)
            }

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

    private suspend fun runFlow(jobUid: String): Either<AppException, Unit> =
        either {
            val jobs = interactor.getJobs().bind()

            val job = jobs.firstOrNull { job -> job.uid == jobUid }
                ?: raise(AppException("Failed to find job by uid: $jobUid"))

            jobUidRef.set(jobUid)
            stateRef.set(RunnerState.RUNNING)
            stepIndex.set(0)

            if (settings.startJobUid == jobUid) {
                settings.startJobUid = null
            }

            interactor.updateJob(
                job.copy(
                    status = JobStatus.RUNNING
                )
            ).bind()

            timeCollector.clear()
            reportCollector.clear()

            runCurrentStep(
                isFirstStep = true,
                initialDelay = DELAY_BETWEEN_STEPS
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
                interactor.updateJob(
                    job.copy(
                        status = JobStatus.CANCELLED,
                        executionTime = timeCollector.getDuration()?.inWholeMilliseconds
                    )
                )
            }
        }
    }

    private suspend fun runCurrentStep(
        isFirstStep: Boolean,
        initialDelay: Long = DELAY_BETWEEN_STEPS
    ): Either<AppException, Unit> =
        either {
            delay(initialDelay)

            if (!isRunning()) {
                raise(AppException("Flow was cancelled"))
            }

            val jobData = interactor.getCurrentJobData().bind()

            val (job, flow, stepEntry, executionData) = jobData

            val command = commandFactory.createCommand(
                flow = flow.entry,
                step = stepEntry.command
            ).bind()

            val nextActionResult = commandExecutor.execute(
                isFirstStep = isFirstStep,
                job = job,
                flow = flow.entry,
                stepEntry = stepEntry,
                command = command,
                stepIndex = stepIndex.get(),
                attemptIndex = executionData.attemptCount
            )
            if (nextActionResult.isLeft()) {
                finishFlowExecution(
                    jobUid = job.uid,
                    isRunNextAllowed = (job.onFinishAction == OnFinishAction.RUN_NEXT)
                ).bind()

                return@either
            }

            val nextAction = nextActionResult.unwrap()
            when (nextAction) {
                is OnStepFinishedAction.Next -> {
                    stepIndex.incrementAndGet()
                    runCurrentStep(
                        isFirstStep = false
                    ).bind()
                }

                OnStepFinishedAction.Complete -> {
                    finishFlowExecution(
                        jobUid = job.uid,
                        isRunNextAllowed = true
                    ).bind()
                }

                OnStepFinishedAction.Retry -> {
                    runCurrentStep(
                        isFirstStep = false
                    ).bind()
                }

                OnStepFinishedAction.Stop -> {
                    finishFlowExecution(
                        jobUid = job.uid,
                        isRunNextAllowed = true
                    ).bind()

                    raise(AppException("Flow was stopped"))
                }
            }
        }

    private suspend fun finishFlowExecution(
        jobUid: String,
        isRunNextAllowed: Boolean
    ): Either<AppException, Unit> =
        either {
            stateRef.set(RunnerState.IDLE)

            val job = interactor.getJobByUid(jobUid).bind()
            val flow = interactor.getCachedFlowByUid(job.flowUid).bind()
            val flowSourceType = flow.entry.sourceType
            val duration = timeCollector.getDuration()
            val reportContent = reportCollector.getLines().joinToString(separator = "\n")

            interactor.saveReport(
                jobUid = job.uid,
                reportContent = reportContent
            ).bind()

            interactor.updateJob(
                job.copy(
                    status = JobStatus.FINISHED,
                    executionTime = duration?.inWholeMilliseconds
                )
            ).bind()

            val isUploaded = when (flowSourceType) {
                SourceType.REMOTE -> {
                    val uploadResult = interactor.uploadJobResult(job.uid)
                    uploadResult.isRight()
                }

                SourceType.LOCAL -> {
                    interactor.moveJobToHistory(jobUid).bind()
                    false
                }
            }

            val isRunNext = (job.onFinishAction == OnFinishAction.RUN_NEXT)

            Timber.d(
                "finishFlowExecution: isUploaded=%s, isRunNext=%s, " +
                    "isRunNextAllowed=%s, sourceType=%s",
                isUploaded,
                isRunNext,
                isRunNextAllowed,
                flowSourceType
            )

            if (isRunNext &&
                isRunNextAllowed &&
                (isUploaded || flowSourceType == SourceType.REMOTE)
            ) {
                scope.launch {
                    runNextFlow()
                }
            } else if (job.onFinishAction == OnFinishAction.SHOW_DETAILS) {
                val intent = MainActivity.createStartIntent(
                    context = context,
                    args = StartArgs(
                        flowUid = job.flowUid,
                        isShowTestRuns = true
                    )
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }

    private suspend fun runStandAloneCommandsIfNeed() {
        if (!isIdle()) {
            return
        }

        while (standaloneCommands.isNotEmpty()) {
            val command = standaloneCommands.remove()
            val result = commandExecutor.executeStandalone(command)
            if (result.isLeft()) {
                Timber.d(result.unwrapError())
            }
        }
    }

    private fun List<JobEntry>.filterByStatus(status: JobStatus): List<JobEntry> {
        return filter { job -> job.status == status }
    }

    enum class RunnerState {
        IDLE,
        RUNNING
    }

    companion object {
        const val DELAY_BETWEEN_STEPS = 1000L // in milliseconds
    }
}